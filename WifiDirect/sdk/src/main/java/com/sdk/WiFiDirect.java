package com.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class WiFiDirect extends BroadcastReceiver implements WifiP2pManager.ChannelListener, WifiP2pManager.PeerListListener
{
    private static Activity gameActivity = null;
    private static Context gameContext = null;
    private final static String TAG = "WiFiDirect";

    private WifiP2pManager mManager;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel mChannel;

    private List<WifiP2pDevice> peers = new ArrayList();
    private List<String> peersStr = new ArrayList<>();


    public static void setGameActivity(Activity activity, Context context)
    {
        gameActivity = activity;
        gameContext = context;
    }

    public void initial()
    {
        //表示Wi-Fi对等网络状态发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        //表示可用的对等点的列表发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        //表示Wi-Fi对等网络的连接状态发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        //设备配置信息发生了改变
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) gameActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(gameContext, gameActivity.getMainLooper(), this);
    }

    public void onResume()
    {
        gameActivity.registerReceiver(this, intentFilter);
    }

    protected void onPause()
    {
        gameActivity.unregisterReceiver(this);
    }


    /*
     * 搜索周围wifiDirect设备
     */
    public void searchNearPeers()
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {
                /*
                 *  查找初始化成功时, 实际上并没有发现任何服务，所以该方法可以置空。
                 *  对等点搜索的代码在onReceive方法中
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

    /*
     * 连接
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


    @Override
    public void onChannelDisconnected()
    {
        MLog.i(TAG, "Wifi Direct disconnect");
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList)
    {
        MLog.i(TAG, "Wifi Direct find available peer");
        peers.clear();
        peersStr.clear();
        Collection<WifiP2pDevice> deviceList = wifiP2pDeviceList.getDeviceList();
        peers.addAll(deviceList);
        for (int i = 0; i < peers.size(); i++)
        {
            peersStr.add(peers.get(i).toString());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            // Broadcast when Wi-Fi Direct is enabled or disabled on the device.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
            }
            else
            {

            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            // 发现周围设备变化 Broadcast when you calldiscoverPeers().
            // mManagerer.requestPeers(mChannel, new MypeerListListener());
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            //连接状态已经改变！需要获取IP
            NetworkInfo device1 = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (device1.isConnected())
            {
                MLog.i(TAG, "onReceive: WIFI_P2P_CONNECTION_CHANGED_ACTION  networkInfo.isConnected()");
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener()
                {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo)
                    {
                        final String ip = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                        MLog.i(TAG, "ip =" + ip);
                    }
                });
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            // Broadcast when a device's details have changed, such as the device's name.
        }
    }
}
