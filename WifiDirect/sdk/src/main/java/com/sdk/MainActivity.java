package com.sdk;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends UnityPlayerActivity implements WiFiDirect.iWifiInterface
{

    private final static String TAG = "MainActivity";
    private WiFiDirect direct = null;
    final String U_TAG = "wifiDirect";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        direct = new WiFiDirect(this, this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MLog.d(TAG, "Game Pause");
        if (direct != null)
        {
            direct.onPause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MLog.d(TAG, "Game Resume " + (direct != null));
        if (direct != null)
        {
            direct.onResume();
        }
    }

    @Override
    protected void onStop()
    {
        if (direct != null) direct.onStop();
        super.onStop();
    }

    public WiFiDirect GetWifiDirect()
    {
        return direct;
    }

    /*
     * 处理 Wifi-Direct 消息， 传给Unity
     */

    @Override
    public void UpdateDevice(String st)
    {
        UnityPlayer.UnitySendMessage(U_TAG, "UpdateDevice", st);
    }

    @Override
    public void UpdatePeers(List<WifiP2pDevice> peers)
    {
        int size = peers.size();
        JSONObject json;
        JSONArray array = new JSONArray();
        try
        {

            for (int i = 0; i < size; i++)
            {
                json = new JSONObject();
                WifiP2pDevice device = peers.get(i);
                json.put("name", device.deviceName);
                json.put("status", device.status);
                json.put("owner", device.isGroupOwner());
                array.put(json);
            }
        }
        catch (JSONException ex)
        {
            MLog.e(TAG, ex.getMessage());
        }
        String str = array.toString();
        MLog.d(TAG, "JAVA JSON: " + str);
        UnityPlayer.UnitySendMessage(U_TAG, "UpdatePeers", str);
    }

    @Override
    public void OnDisconnect()
    {
        UnityPlayer.UnitySendMessage(U_TAG, "OnDisconnect", "");
    }

    @Override
    public void ReciveMsg(String msg)
    {
        MLog.d(WiFiDirect.TAG, msg);
//        Toast.makeText(this, "server: " + msg, Toast.LENGTH_SHORT).show();
        UnityPlayer.UnitySendMessage(U_TAG, "ReciveMsg", msg);
    }
}
