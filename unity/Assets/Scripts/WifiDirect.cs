using System.Collections;
using System.Collections.Generic;

namespace mulitypeer
{
    using UnityEngine;

    public class WifiDirect : MonoBehaviour
    {
        private static AndroidJavaClass _unityPlayerClass;
        private const string TAG = "wifiDirect";
        private static GameObject GO;

        private static ERecvMessageHandler eRecvMessageHandler;
        private static ERoleQuitHandler eRoleQuitHandler;

        public static AndroidJavaClass UnityPlayerClass
        {
            get
            {
                if (_unityPlayerClass == null)
                {
                    _unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
                }

                return _unityPlayerClass;
            }
        }


        private static AndroidJavaObject _currentActivity;

        public static AndroidJavaObject CurrentActivity
        {
            get
            {
                if (_currentActivity == null)
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
                if (_wifiDirect == null)
                {
                    _wifiDirect = CurrentActivity.Call<AndroidJavaObject>("GetWifiDirect");
                }

                return _wifiDirect;
            }
        }

        public static void Initial(ERecvMessageHandler recvHandler, ERoleQuitHandler quitHandler)
        {
            eRecvMessageHandler = recvHandler;
            eRoleQuitHandler = quitHandler;
            AndroidWifiDirect.Call("Initial");
            AttachCamera();
        }

        public static void BroadcastMsg(string msg)
        {
            Debug.Log("broad cast: " + msg);
            AndroidWifiDirect.Call("createClientThread", msg);
        }

        public static void QuitConnect()
        {
            AndroidWifiDirect.Call("Disconnect");
        }

        public static void OpenSetting()
        {
            AndroidWifiDirect.Call("EnableSetting");
        }

        public static void Discover()
        {
            AndroidWifiDirect.Call("SearchNearPeers");
        }

        public static void Connect(int pos)
        {
            AndroidWifiDirect.Call("Connect", pos);
        }

        private static void AttachCamera()
        {
            GO = GameObject.Find(TAG);
            if (GO == null)
            {
                GO = new GameObject(TAG);
                GO.AddComponent<WifiDirect>();
            }
        }


        List<string> mPeers = new List<string>();

        public void OnGUI()
        {
            for (int i = 0; i < mPeers.Count; i++)
            {
                if (GUI.Button(new Rect(400, 300 + 100 * i, 300, 80), mPeers[i]))
                {
                    Connect(i);
                }
            }
        }


        // Native Callback
        public void UpdateDevice(string st)
        {
            Debug.Log("device info: " + st);
        }

        public void UpdatePeers(string peers)
        {
            ArrayList ps = (ArrayList) MiniJSON.jsonDecode(peers);
            Debug.Log("peer count:" + ps.Count);
            mPeers.Clear();
            for (int i = 0; i < ps.Count; i++)
            {
                Hashtable it = (Hashtable) ps[i];
                mPeers.Add(it["name"] + " " + it["status"] + "\n");
            }
        }

        public void OnDisconnect(string param)
        {
            Debug.Log("wifi-direct disconnect");
            eRoleQuitHandler?.Invoke(param);
        }

        public void ReciveMsg(string msg)
        {
            Debug.Log(msg);
            eRecvMessageHandler?.Invoke(msg);
        }
    }
}
