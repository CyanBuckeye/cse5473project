package com.project.cse5473.mafia;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class JoinGameActivity extends AppCompatActivity {

    DataOutputStream socketWrite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Join Game");
    }

    // attempt to join the game
    public void joinGame(View v) {
        // get username and ip address
        String hostIP = ((EditText) findViewById(R.id.host_ip_input)).getText().toString();
        String username = ((EditText) findViewById(R.id.username_input)).getText().toString();
        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // setup socket
        Socket s = setupSocket(hostIP);
        try {
            socketWrite = new DataOutputStream(s.getOutputStream());
        } catch(IOException e) {
            Log.d("Client Socket", e.toString());
        }

        // join game
        sendJoinMessage(username);
    }


    // send join message to host
    public void sendJoinMessage(String username) {
        if (socketWrite != null) {
            try {
                socketWrite.writeUTF(username);
                socketWrite.flush();
            } catch (IOException e) {
                Log.d("Client Socket", e.toString());
            }
        }
    }

    // create socket conencted to host
    public Socket setupSocket(String ipAddress) {
        Socket sock = null;
        try {
            sock = new Socket(ipAddress, 5473);
        } catch (IOException e) {
            Log.d("ClientSocket", e.toString());
        }
        return sock;
    }
}
