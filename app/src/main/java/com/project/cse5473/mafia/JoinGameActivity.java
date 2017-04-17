package com.project.cse5473.mafia;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class JoinGameActivity extends AppCompatActivity {

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

        // set up socket connection
        createClientSocket(hostIP);

        // join game
        sendJoinMessage(username);
    }

    // create client side socket connection
    public void createClientSocket(String ipAddress) {

    }

    // send join message to host
    public void sendJoinMessage(String username) {

    }
}
