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


    public ServerThread(WiFiDirect direct, int port)
    {
        this.mPort = port;
    }

    @Override
    public void run()
    {
        try
        {
            // Create new server socket
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
                // Perform game-related processing
                JSONObject fromClient = null;
                try
                {
                    fromClient = new JSONObject(mFromClient);
//                    Game curGame = mDirect.getGame();
                    int type = fromClient.getInt("type");
                    if (type == 0)
                    {
//                        curGame.tilemap.updateUnit(fromClient.getInt("newX"), fromClient.getInt("newY"),
//                                fromClient.getInt("prevX"), fromClient.getInt("prevY"));
                    }
                    else
                    {
//                        curGame.curTurn = fromClient.getInt("turn");
//                        curGame.displayTurnReady();
//                        Toast.makeText(mDirect.getApplicationContext(), "Your turn", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    System.err.println("data send failure");
                    MLog.d(WiFiDirect.TAG, "Receive coord failure: " + e.toString());
                }
            }
        });
    }

    // Lets MainActivity stop thread
    public ServerSocket getServerSocket()
    {
        return mServerSocket;
    }
}
