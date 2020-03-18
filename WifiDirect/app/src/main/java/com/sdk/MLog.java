package com.sdk;

import android.util.Log;

/**
 * @author penghuailiang
 * log的封装实现
 */

public class MLog
{
    private static boolean VERBOSE = true;
    private static boolean DEBUG = true;
    private static boolean INFO = true;
    private static boolean WARN = true;
    private static boolean ERR = true;


    public static void v(String t, String m)
    {
        if (VERBOSE)
            Log.v(t, m);
    }


    public static void v(String t, String m, Throwable w)
    {
        if (VERBOSE)
            Log.v(t, m, w);
    }

    public static void d(String t, String m)
    {
        if (DEBUG)
            Log.d(t, m);
    }

    public static void d(String t, String m, Throwable w)
    {
        if (DEBUG)
            Log.d(t, m, w);
    }

    public static void i(String t, String m)
    {
        if (INFO)
            Log.i(t, m);
    }

    public static void i(String t, String m, Throwable w)
    {
        if (INFO)
            Log.i(t, m, w);
    }

    public static void w(String t, String m)
    {
        if (WARN)
            Log.w(t, m);
    }

    public static void w(String t, String m, Throwable w)
    {
        if (WARN)
            Log.w(t, m, w);
    }

    public static void e(String t, String m)
    {
        if (ERR)
            Log.e(t, m);
    }

    public static void e(String t, String m, Throwable w)
    {
        if (ERR)
            Log.e(t, m, w);
    }
}