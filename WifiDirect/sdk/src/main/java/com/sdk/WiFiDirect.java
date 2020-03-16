package com.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import java.util.ArrayList;


public class WiFiDirect implements WifiP2pManager.ChannelListener
{
    public static volatile String PeerAddress = null;
    public final static String TAG = "WiFiDirect";
    public static Activity GameActivity = null;
    private static Context gameContext = null;

    final int CLIENT_PORT = 8900;
    final int SERVER_PORT = 8800;

    private WifiP2pManager mManager;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel mChannel;
    private WifiDirectReceiver mReceiver;
    private ArrayList<WifiP2pDevice> peers = new ArrayList();

    private Thread mServerThread = null;
    private Thread mClientThread = null;
    private boolean mIsGroupOwner = false;
    private boolean mWifiP2pEnabled = false;

    public static void setGameActivity(Activity activity, Context context)
    {
        GameActivity = activity;
        gameContext = context;
    }

    public void Initial()
    {
        //表示Wi-Fi对等网络状态发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        //表示可用的对等点的列表发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        //表示Wi-Fi对等网络的连接状态发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        //设备配置信息发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) GameActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(gameContext, GameActivity.getMainLooper(), this);
        mReceiver = new WifiDirectReceiver(mManager, mChannel, this);

        SearchNearPeers();
    }

    public void onResume()
    {
        GameActivity.registerReceiver(mReceiver, intentFilter);
    }

    public void onPause()
    {
        GameActivity.unregisterReceiver(mReceiver);
    }

    public void onStop()
    {
        Disconnect();
    }

    public ArrayList<WifiP2pDevice> getArrayList()
    {
        return peers;
    }

    /*
     * 搜索周围wifiDirect设备, 处理会到WifiDirectReceiver.onReceive
     */
    public void SearchNearPeers()
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {
                /*
                 *  查找初始化成功时, 实际上并没有发现任何服务，所以该方法可以置空。
                 *  对等点搜索的代码在处理会到WifiDirectReceiver.onReceive方法中
                 */
            }

            @Override
            public void onFailure(int i)
            {
                /*
                 * 找初始化失败时的处理写在这里
                 */
                MLog.e(TAG, "FAILED TO SEARCH PEER");
            }
        });
    }

    private void CreateGroup()
    {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {
                setIsGroupOwner(true);
            }

            @Override
            public void onFailure(int reason)
            {
                MLog.d(TAG, "Create group failure. Reason: " + reason);
            }

        });
    }

    /*
     * 连接 （先创建组后才能连接）
     */
    public void Connect(int pos)
    {
        WifiP2pDevice peer = peers.get(pos);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = 0;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener()
        {

            @Override
            public void onSuccess()
            {
                //连接成功
            }

            @Override
            public void onFailure(int i)
            {
                MLog.e(TAG, "connect failed");
            }
        });
    }

    public void Disconnect()
    {
        if (mManager != null)
        {
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener()
            {
                @Override
                public void onSuccess()
                {
                }

                @Override
                public void onFailure(int reason)
                {
                    Log.d(TAG, "Remove group failure. Reason: " + reason);
                }
            });
            mManager.cancelConnect(mChannel, null);
            peers.clear();
        }
        else
        {
            MLog.e(TAG, "mManager not initial");
        }
    }

    public void OnPeersChanged()
    {
        if (peers.size() <= 0)
        {
            MLog.i(TAG,"peers is empty, it will be create new group");
            CreateGroup();
        }
        else
        {
            // notify unity's show peers
            MLog.i(TAG, "peers count: "+peers.size());
        }
    }

    public void setIsWifiP2pEnabled(boolean enabled) {
        mWifiP2pEnabled = enabled;
    }

    public void setIsGroupOwner(boolean isOwner)
    {
        mIsGroupOwner = isOwner;
    }

    public boolean isGroupOwner()
    {
        return mIsGroupOwner;
    }

    // Create sending threads, return after sending
    public void createClientThread(String msg)
    {
        if (mIsGroupOwner)
            mClientThread = new ClientThread(msg, CLIENT_PORT);
        else
            mClientThread = new ClientThread(msg, SERVER_PORT);
        new Thread(mClientThread).start();
    }

    // Create listening threads, always on
    public void createServerThread()
    {
        if (mIsGroupOwner)
            mServerThread = new ServerThread(SERVER_PORT);
        else
            mServerThread = new ServerThread(CLIENT_PORT);
        new Thread(mServerThread).start();
    }


    @Override
    public void onChannelDisconnected()
    {
        MLog.i(TAG, "Wifi Direct disconnect");
    }

}
