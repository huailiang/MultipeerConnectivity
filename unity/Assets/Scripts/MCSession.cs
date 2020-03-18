/*
 * Unity3D与iOS的交互
 *  https://www.jianshu.com/p/1ab65bee6692
 */

using System;
using System.Runtime.InteropServices;

public class MCSession
{
    public delegate void RoleJoinHandler(IntPtr name);

    public delegate void RecvMessageHandler(IntPtr msg);

    public delegate void RoleQuitHandler(IntPtr name);


    [DllImport("__Internal")]
    static extern void InitWith(string name, string type, RoleJoinHandler roleHandler, RecvMessageHandler recvHandler,
        RoleQuitHandler quitHandler);

    [DllImport("__Internal")]
    static extern void Broadcast(string msg);

    [DllImport("__Internal")]
    static extern void QuitConnect();


    public static void U3D_InitWith(string name, string type, RoleJoinHandler roleHandler,
        RecvMessageHandler recvHandler, RoleQuitHandler quitHandler)
    {
#if UNITY_IOS
        InitWith(name, type, roleHandler, recvHandler, quitHandler);
#elif UNITY_ANDROID
        WifiDirect.Initial();
#endif
    }

    public static void U3D_Broadcast(string msg)
    {
#if UNITY_IOS
        Broadcast(msg);
#elif UNITY_ANDROID
        WifiDirect.BroadcastMsg(msg);
#endif
    }

    public static void U3D_Quit()
    {
#if UNITY_IOS
        QuitConnect();
#elif UNITY_ANDROID
        WifiDirect.QuitConnect();
#endif
    }
}
