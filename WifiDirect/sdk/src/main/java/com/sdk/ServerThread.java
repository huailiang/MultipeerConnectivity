package com.sdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread extends Thread
{
    private String mFromClient = "";
    private int mPort;
    private ServerSocket mServerSocket = null;


    ServerThread(int port)
    {
        this.mPort = port;
    }

    @Override
    public void run()
    {
        try
        {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(mPort));
            Socket clientSocket = null;

            // Loop until interrupted/killed
            currentThread();
            while (!Thread.interrupted())
            {
                // Accept client connection, will block
                clientSocket = mServerSocket.accept();

                // Set peer IP address (group owner only)
                if (WiFiDirect.PeerAddress == null)
                {
                    String peerAddress = clientSocket.getInetAddress().getHostAddress();
                    WiFiDirect.PeerAddress = peerAddress;
                    WiFiDirect.GameActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                        }

                    });
                }

                // Read client input
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                mFromClient = in.readLine();

                updateView();

                in.close();
                clientSocket.close();
            }

            if (clientSocket != null)
                clientSocket.close();
            mServerSocket.close();
        }
        catch (IOException e)
        {
            // Server socket can be closed by main thread, ServerSocket.accept() will throw exception
            // and thread will be stopped here
            Log.d(WiFiDirect.TAG, "Server thread exception: " + e.toString());
            return;
        }
    }

    private void updateView()
    {
        WiFiDirect.GameActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
               MLog.d(WiFiDirect.TAG, mFromClient);
            }
        });
    }

    public ServerSocket getServerSocket()
    {
        return mServerSocket;
    }
}
