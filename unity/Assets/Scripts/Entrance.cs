using System;
using System.Runtime.InteropServices;
using AOT;
using UnityEngine;

public class Entrance : MonoBehaviour
{
    private static string content = "";
    private string input = "input: ";

    void OnGUI()
    {
        if (GUI.Button(new Rect(10, 10, 120, 80), "Connect"))
        {
            MCSession.U3D_InitWith(SystemInfo.deviceName, "HUAILIANG", OnPeerJoin, OnRecvMessage, OnPeerQuit);
        }

        if (GUI.Button(new Rect(210, 10, 120, 80), "Quit"))
        {
            MCSession.U3D_Quit();
        }

        if (GUI.Button(new Rect(410, 10, 120, 80), "Toast"))
        {
            WifiDirect.ShowToast("toast: hello world");
        }


        if (GUI.Button(new Rect(610, 10, 120, 80), "Dial"))
        {
            WifiDirect.ShowDialog("dialog: hello world");
        }

        input = GUI.TextField(new Rect(10, 120, 260, 40), input);
        if (GUI.Button(new Rect(280, 120, 80, 40), "Send"))
        {
            Debug.Log("send msg: " + input);
            MCSession.U3D_Broadcast(input);
            input = string.Empty;
        }

        GUI.Label(new Rect(10, 180, 500, 300), content);
    }


    [MonoPInvokeCallback(typeof(MCSession.RoleJoinHandler))]
    static void OnPeerJoin(IntPtr ptr)
    {
        string name = Marshal.PtrToStringAnsi(ptr);
        Debug.Log("u3d role join with name: " + name);
    }

    [MonoPInvokeCallback(typeof(MCSession.RoleJoinHandler))]
    static void OnRecvMessage(IntPtr ptr)
    {
        string msg = Marshal.PtrToStringAnsi(ptr);
        Debug.Log("u3d receive message: " + msg);
        content += msg + "\n";
    }

    [MonoPInvokeCallback(typeof(MCSession.RoleJoinHandler))]
    static void OnPeerQuit(IntPtr ptr)
    {
        string name = Marshal.PtrToStringAnsi(ptr);
        Debug.Log("u3d role quit with name: " + name);
    }
}
