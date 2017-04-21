package com.project.cse5473.mafia;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.*;

class SocketSendTask extends AsyncTask<String, String, Void> {
    public SocketSendTask() {
        super();
    }

    @Override
    protected Void doInBackground(String... pParams) {

        // create the socket
        Socket socket = setupSocket(pParams[0], 9999, 5000);
        JSONObject msg = new JSONObject();

        // create appropriate json object for the action being sent
        if (pParams[1] == "join") {
            msg = new JSONObject();
            try {
                msg.put("username", pParams[2]);
                msg.put("action", pParams[1]);
            } catch (JSONException je) {
                Log.d("Client Socket", je.toString());
            }
        }

        // send json object
        sendJson(msg, socket);

        return null;
    }

    // sends the json object over the socket
    private void sendJson(JSONObject json, Socket s) {
        try{
            DataOutputStream socketWrite = new DataOutputStream(s.getOutputStream());
            PrintWriter pw = new PrintWriter(socketWrite);
            pw.println(json.toString());
            pw.flush();
        } catch(IOException e) {
            Log.d("Client Socket", e.toString());
        }
    }

    // creates the socket with the given ip address, port, and timeout
    private Socket setupSocket(String ipAddress, int port, int timeout) {
        Socket sock = null;
        try {
            InetSocketAddress inetAddress = new InetSocketAddress(ipAddress, port);
            sock = new Socket();
            sock.connect(inetAddress, timeout);
        } catch (IOException e) {
            Log.d("ClientSocket", e.toString());
        }
        return sock;
    }
}
