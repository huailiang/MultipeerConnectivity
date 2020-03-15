using System;
using System.Runtime.InteropServices;
using AOT;
using UnityEngine;

public class Entrance : MonoBehaviour
{

    private string content;
    
    private void Start()
    {
        Debug.Log("App Start");
    }

    void OnGUI()
    {
        if (GUI.Button(new Rect(10,10,100,80), "Connect"))
        {
            Debug.Log("connect");
            MCSession.InitWith(SystemInfo.deviceName,"HUAILIANG");
        }

        if (GUI.Button(new Rect(10,120,100,80),"Send" ))
        {
            Debug.Log("send msg");
            MCSession.Broadcast("hello world");
        }
        
        GUI.Label(new Rect(300,10, 500, 300), content);
    }

    [MonoPInvokeCallback(typeof(MCSession.NewRoleJoinHandler))]
    public void OnPeerJoin(string name)
    {
        Debug.Log("new role join with name: "+name);
    }
}
