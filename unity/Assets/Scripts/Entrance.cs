using UnityEngine;
using mulitypeer;


public class Entrance : MonoBehaviour
{
    private static string content = "";
    private string input = "input: ";

    void OnGUI()
    {
        if (GUI.Button(new Rect(10, 10, 140, 80), "Connect"))
        {
            MCSession.U3D_InitWith(SystemInfo.deviceName, "HUAILIANG", OnPeerJoin, OnRecvMessage, OnPeerQuit);
        }

        if (GUI.Button(new Rect(210, 10, 140, 80), "Quit"))
        {
            MCSession.U3D_Quit();
        }
#if UNITY_ANDROID
        if (GUI.Button(new Rect(410, 10, 140, 80), "Discover"))
        {
            WifiDirect.Discover();
        }

        if (GUI.Button(new Rect(610, 10, 140, 80), "Setting"))
        {
            WifiDirect.OpenSetting();
        }
#endif

        input = GUI.TextField(new Rect(10, 140, 260, 80), input);
        if (GUI.Button(new Rect(280, 140, 150, 80), "Send"))
        {
            Debug.Log("send msg: " + input);
            MCSession.U3D_Broadcast(input);
            input = string.Empty;
        }

        GUI.Label(new Rect(10, 240, 500, 300), content);
    }


    static void OnPeerJoin(string name)
    {
        content += "role join: " + name + "\n";
        Debug.Log("u3d role join with name: " + name);
    }


    static void OnRecvMessage(string msg)
    {
        Debug.Log("u3d receive message: " + msg);
        content += msg + "\n";
    }

    static void OnPeerQuit(string name)
    {
        Debug.Log("u3d role quit with name: " + name);
        content += "role quit:" + name + "\n";
    }
}
