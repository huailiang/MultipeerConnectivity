package com.sdk;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread
{
    private String mMessage;
    private int mPort;

    public ClientThread(String msg, int port)
    {
        this.mMessage = msg;
        this.mPort = port;
    }


    @Override
    public void run()
    {
        try
        {
            // Stop thread if unable to send
            if (WiFiDirect.PeerAddress == null)
            {
                WiFiDirect.GameActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                    }

                });

                return;
            }

            Socket socket = new Socket(WiFiDirect.PeerAddress, mPort);

            // Send client output
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(mMessage);

            out.close();
            socket.close();
        }
        catch (IOException e)
        {
            MLog.d(WiFiDirect.TAG, "Client thread exception: " + e.toString());
            return;
        }
    }

}
