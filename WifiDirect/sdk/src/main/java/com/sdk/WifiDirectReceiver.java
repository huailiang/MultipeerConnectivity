package com.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.Toast;

import java.util.ArrayList;

public class WifiDirectReceiver extends BroadcastReceiver implements PeerListListener
{

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiDirect mDirect;
    private ArrayList<WifiP2pDevice> peers;

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList)
    {
        peers = mDirect.getArrayList();
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        // notify msg to unity or main_activity
        if (mDirect != null) mDirect.onChannelDisconnected();
    }


    public WifiDirectReceiver(WifiP2pManager manager, Channel channel, WiFiDirect direct)
    {
        this.mManager = manager;
        this.mChannel = channel;
        this.mDirect = direct;
    }


    @Override
    public void onReceive(final Context context, Intent intent)
    {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            // 当设备的 WifiDirect 打开或关闭时进行广播
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                mDirect.setIsWifiP2pEnabled(true);
                mDirect.SearchNearPeers();
                if (mDirect != null)
                    mManager.requestPeers(mChannel, this);
            }
            else
            {
                mDirect.setIsWifiP2pEnabled(false);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            // 当调用discoverPeers()方法的时候进行广播
            if (mManager != null)
                mManager.requestPeers(mChannel, this);
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            // 当设备的Wi-Fi连接信息状态改变时候进行广播
            NetworkInfo netinfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (netinfo.isConnected())
            {
                final PeerListListener listener = this;
                MLog.i(WiFiDirect.TAG, "onReceive: WIFI_P2P_CONNECTION_CHANGED_ACTION  networkInfo.isConnected()");
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener()
                {

                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info)
                    {
                        // Get group owner IP address
                        String ownerAddr = info.groupOwnerAddress.getHostAddress();

                        mManager.requestPeers(mChannel, listener);

                        // Create server and client threads in MainActivity
                        if (info.groupFormed && info.isGroupOwner)
                        {
                            Toast.makeText(context, "You are on Red Team", Toast.LENGTH_SHORT).show();
                            mDirect.setIsGroupOwner(true);
                            mDirect.createServerThread();
                        }
                        else if (info.groupFormed)
                        {
                            Toast.makeText(context, "You are on Blue Team", Toast.LENGTH_SHORT).show();
                            mDirect.setIsGroupOwner(false);
                            // Peer is group owner
                            mDirect.PeerAddress = ownerAddr;
                            mDirect.createServerThread();
                            mDirect.createClientThread("\"I challenge you!!\"");
                        }
                        else
                        {
                            Toast.makeText(context, "Group not formed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            // 当设备的详细信息改变的时候进行广播，比如设备的名称.
        }
    }
}
