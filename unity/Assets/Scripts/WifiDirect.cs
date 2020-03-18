using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class WifiDirect
{
    private static AndroidJavaClass _unityPlayerClass;

    public static AndroidJavaClass UnityPlayerClass
    {
        get
        {
            if (_unityPlayerClass==null)
            {
                _unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            }
            return  _unityPlayerClass;
        }
    }


    private static AndroidJavaObject _currentActivity;

    public static AndroidJavaObject CurrentActivity
    {
        get
        {
            if (_currentActivity==null)
            {
                _currentActivity = UnityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity");
            }

            return _currentActivity;
        }
    }

    private static AndroidJavaObject _wifiDirect;

    public static AndroidJavaObject AndroidWifiDirect
    {
        get
        {
            if (_wifiDirect==null)
            {
                _wifiDirect = CurrentActivity.Call<AndroidJavaObject>("GetWifiDirect");
            }
            return _wifiDirect;
        }
    }

    public static void Initial()
    {
        AndroidWifiDirect.Call("Initial");
    }

    public static void BroadcastMsg(string msg)
    {
        AndroidWifiDirect.Call("createClientThread", msg);
    }

    public static void QuitConnect()
    {
        AndroidWifiDirect.Call("Disconnect");
    }

    public static void ShowToast(string msg)
    {
        AndroidWifiDirect.Call("ShowToast",msg);
    }

    public static void ShowDialog(string msg)
    {
        AndroidWifiDirect.Call("ShowDialog", msg);
    }
}
