package com.project.cse5473.mafia;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.HttpURLConnection;
import android.util.DebugUtils;
import java.lang.Exception;
import android.os.Debug;
import org.json.*;
/**
 * Created by Administrator on 2017/4/20.
 */

//new class needed for HTTP request. Using asynctask class.
class SocketSendTask extends AsyncTask<String, String, Void>//the second parameter stands for the HTTP request, such as GET or POST,
    // the first one stands for the message you want to transfer to the server; the third one is type of returning value
{
    public SocketSendTask() {
        super();
    }

    @Override
    protected Void doInBackground(String...pParams) {

        Socket socket = setupSocket(pParams[1]);
        DataOutputStream socketWrite = null;
        try {
            socketWrite = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            Log.d("Client Socket", e.toString());
        }

        // send join message
        if (socketWrite != null) {
            try {
                socketWrite.writeUTF("{action: \"join\", username: \"" + pParams[2] + "\"}");
                socketWrite.flush();
            } catch (IOException e) {
                Log.d("Client Socket", e.toString());
            }
        }

        return null;
    }

    public Socket setupSocket(String ipAddress) {
        Socket sock = null;
        try {
            int timeout = 5000;
            int port = 9999;
            InetSocketAddress inetAddress = new InetSocketAddress(ipAddress, port);
            sock = new Socket();
            sock.connect(inetAddress, timeout);
        } catch (IOException e) {
            Log.d("ClientSocket", e.toString());
        }
        return sock;
    }
}
