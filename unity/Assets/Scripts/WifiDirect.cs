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
            return _unityPlayerClass ?? (_unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"));
        }
    }


    private static AndroidJavaObject _currentActivity;

    public static AndroidJavaObject CurrentActivity
    {
        get
        {
            return _currentActivity ??
                   (_currentActivity = UnityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity"));
        }
    }

    private static AndroidJavaObject _wifiDirect;

    public static AndroidJavaObject AndroidWifiDirect
    {
        get
        {
            return _wifiDirect ?? (_wifiDirect = CurrentActivity.Call<AndroidJavaObject>("GetWifiDirect"));
        }
    }

    public static void Initial()
    {
        AndroidWifiDirect.Call("Initial");
    }

    public static void BroadcastMsg(string msg)
    {
        AndroidWifiDirect.Call("createClientThread",msg);
    }
}
