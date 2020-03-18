/*
 * Unity3D与iOS的交互
 *  https://www.jianshu.com/p/1ab65bee6692
 */

using AOT;

namespace mulitypeer
{
    using System;
    using System.Runtime.InteropServices;

    public delegate void ERoleJoinHander(string name);

    public delegate void ERecvMessageHandler(string msg);

    public delegate void ERoleQuitHandler(string name);

    public class MCSession
    {
        public delegate void RoleJoinHandler(IntPtr name);

        public delegate void RecvMessageHandler(IntPtr msg);

        public delegate void RoleQuitHandler(IntPtr name);


        [DllImport("__Internal")]
        static extern void InitWith(string name, string type, RoleJoinHandler roleHandler,
            RecvMessageHandler recvHandler, RoleQuitHandler quitHandler);

        [DllImport("__Internal")]
        static extern void Broadcast(string msg);

        [DllImport("__Internal")]
        static extern void QuitConnect();

        private static ERoleJoinHander eRoleJoinHander;
        private static ERecvMessageHandler eRecvMessageHandler;
        private static ERoleQuitHandler eRoleQuitHandler;

        public static void U3D_InitWith(string name, string type, ERoleJoinHander roleHandler,
            ERecvMessageHandler recvHandler, ERoleQuitHandler quitHandler)
        {
            eRoleJoinHander = roleHandler;
            eRecvMessageHandler = recvHandler;
            eRoleQuitHandler = quitHandler;

#if UNITY_IOS
            InitWith(name, type, OnPeerJoin, OnRecvMessage, OnPeerQuit);
#elif UNITY_ANDROID
            WifiDirect.Initial(eRecvMessageHandler, eRoleQuitHandler);
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

        [MonoPInvokeCallback(typeof(MCSession.RoleJoinHandler))]
        static void OnPeerJoin(IntPtr ptr)
        {
            string name = Marshal.PtrToStringAnsi(ptr);
            eRoleJoinHander?.Invoke(name);
        }

        [MonoPInvokeCallback(typeof(MCSession.RoleJoinHandler))]
        static void OnRecvMessage(IntPtr ptr)
        {
            string msg = Marshal.PtrToStringAnsi(ptr);
            eRecvMessageHandler?.Invoke(msg);
        }

        [MonoPInvokeCallback(typeof(MCSession.RoleJoinHandler))]
        static void OnPeerQuit(IntPtr ptr)
        {
            string name = Marshal.PtrToStringAnsi(ptr);
            eRoleQuitHandler?.Invoke(name);
        }
    }
}
