package com.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class WiFiDirect implements WifiP2pManager.ChannelListener
{

    public interface iWifiInterface
    {
        void UpdateDevice(String st);

        void UpdatePeers(List<WifiP2pDevice> peers);

        void OnDisconnect();

        void ReciveMsg(String msg);
    }

    public volatile String PeerAddress = null;
    public final static String TAG = "WiFiDirect";
    public Activity GameActivity;
    private Context gameContext;
    private iWifiInterface iWifi;
    private ServerThread mServerThread;

    private final int CLIENT_PORT = 8900;
    private final int SERVER_PORT = 8800;

    private WifiP2pManager mManager;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel mChannel;
    private WifiDirectReceiver mReceiver;
    private ProgressDialog progressDialog;
    private List<WifiP2pDevice> peers = new ArrayList<>();

    private boolean mIsGroupOwner = false;
    private boolean mWifiP2pEnabled = false;

    public WiFiDirect(Activity activity, iWifiInterface bridge)
    {
        GameActivity = activity;
        gameContext = activity;
        iWifi = bridge;
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

    public List<WifiP2pDevice> getArrayList()
    {
        return peers;
    }

    private String getDeviceStatus(int deviceStatus)
    {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus)
        {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    public String getDevicesInfo(WifiP2pDevice device)
    {
        return device.deviceName + " " + getDeviceStatus(device.status) + " owner:" + device.isGroupOwner();
    }

    public void EnableSetting()
    {
       GameActivity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    /*
     * 搜索周围wifiDirect设备, 处理会到WifiDirectReceiver.onReceive
     */
    public void SearchNearPeers()
    {
        if (!mWifiP2pEnabled)
        {
            Toast.makeText(gameContext, "Enable P2P form action bar button", Toast.LENGTH_SHORT).show();
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        progressDialog = ProgressDialog.show(gameContext, "Press back cancel", "find peers", true, true);

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
                Toast.makeText(gameContext, "Connect success", Toast.LENGTH_SHORT).show();
                MLog.d(TAG, "connect success");
            }

            @Override
            public void onFailure(int i)
            {
                Toast.makeText(gameContext, "Connect failed", Toast.LENGTH_SHORT).show();
                MLog.e(TAG, "connect failed");
            }
        });
    }

    public void Disconnect()
    {
        if (mManager != null)
        {
            // 处于Invited时可以取消邀请
            mManager.cancelConnect(mChannel, null);
            killServer();
            peers.clear();
            iWifi.UpdatePeers(peers);
        }
        else
        {
            MLog.e(TAG, "mManager not initial");
        }
    }

    public void OnPeersChanged()
    {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        if (peers.size() <= 0)
        {
            String str = "peers is empty, it will be create new group";
            MLog.i(TAG, str);
            Toast.makeText(gameContext, str, Toast.LENGTH_LONG).show();
        }
        else
        {
            MLog.i(TAG, "peers count: " + peers.size());
            iWifi.UpdatePeers(peers);
        }
    }

    public void setIsWifiP2pEnabled(boolean enabled)
    {
        mWifiP2pEnabled = enabled;
        if (!enabled)
        {
            peers.clear();
            iWifi.UpdatePeers(peers);
        }
    }

    public void setIsGroupOwner(boolean isOwner)
    {
        mIsGroupOwner = isOwner;
    }

    public boolean isGroupOwner()
    {
        return mIsGroupOwner;
    }

    public void UpdateDevice(WifiP2pDevice device)
    {
        String info = getDevicesInfo(device);
        iWifi.UpdateDevice(info);
    }

    public void ReceiveMsg(String msg)
    {
        GameActivity.runOnUiThread(() ->
        {
            iWifi.ReciveMsg(msg);
        });
    }

    // Create sending threads, return after sending
    public void createClientThread(String msg)
    {
        Thread mClientThread;
        if (mIsGroupOwner)
            mClientThread = new ClientThread(msg, this, CLIENT_PORT);
        else
            mClientThread = new ClientThread(msg, this, SERVER_PORT);
        new Thread(mClientThread).start();
    }

    // Create listening threads, always on
    public void createServerThread()
    {
        if (mIsGroupOwner)
            mServerThread = new ServerThread(this, SERVER_PORT);
        else
            mServerThread = new ServerThread(this, CLIENT_PORT);
        new Thread(mServerThread).start();
    }

    public void killServer()
    {
        if (mServerThread != null)
        {
            try
            {
                ServerSocket socket = mServerThread.getServerSocket();
                if (socket != null && !socket.isClosed())
                {
                    socket.close();
                }
            }
            catch (IOException e)
            {
                MLog.d(WiFiDirect.TAG, "Server thread exception: " + e.toString());
            }
            mServerThread.interrupt();
            MLog.e(TAG, "wifi direct server quit");
        }
    }


    @Override
    public void onChannelDisconnected()
    {
        iWifi.OnDisconnect();
    }

}
