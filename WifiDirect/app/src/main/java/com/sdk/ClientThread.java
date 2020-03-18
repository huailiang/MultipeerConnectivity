package com.sdk;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread
{
    private String mMessage;
    private int mPort;
    private WiFiDirect mDirect;

    public ClientThread(String msg, WiFiDirect direct, int port)
    {
        this.mMessage = msg;
        this.mPort = port;
        this.mDirect = direct;
    }


    @Override
    public void run()
    {
        try
        {
            // Stop thread if unable to send
            if (mDirect.PeerAddress == null)
            {
                mDirect.GameActivity.runOnUiThread(() -> {
                });

                return;
            }

            Socket socket = new Socket(mDirect.PeerAddress, mPort);

            // Send client output
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(mMessage);

            out.close();
            socket.close();
        }
        catch (IOException e)
        {
            MLog.d(WiFiDirect.TAG, "Client thread exception: " + e.toString());
        }
    }

}
