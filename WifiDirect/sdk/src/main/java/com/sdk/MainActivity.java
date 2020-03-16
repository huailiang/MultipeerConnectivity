package com.sdk;

import android.content.Context;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity
{

    private final static String TAG = "MainActivity";
    private Context mContext = null;
    private WiFiDirect direct = null;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        direct = new WiFiDirect();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MLog.d(TAG, "Game Pause");
        if (direct != null)
        {
            direct.onPause();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MLog.d(TAG, "Game Resume");
        if (direct!=null)
        {
            direct.onResume();
        }
    }

    @Override
    protected void onStop()
    {
        if (direct!=null) direct.onStop();
        super.onStop();
    }


}
