/*
 * Unity3D与iOS的交互
 *  https://www.jianshu.com/p/1ab65bee6692
 */

using System;
using AOT;
using UnityEngine;
using System.Runtime.InteropServices;

public class MCSession 
{
    [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
    public delegate void NewRoleJoinHandler(string resultString);
    
    [DllImport("__Internal")]
    public static extern void InitWith (string name, string type);
    
    [DllImport("__Internal")]
    public static extern void Broadcast (string msg);
    
}
