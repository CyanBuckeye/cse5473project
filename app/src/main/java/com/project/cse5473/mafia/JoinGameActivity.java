package com.project.cse5473.mafia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
    public void joinGameSockets(View v) {
        // get username and ip address
        String hostIP = ((EditText) findViewById(R.id.host_ip_input)).getText().toString();
        String username = ((EditText) findViewById(R.id.username_input)).getText().toString();
        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // setup socket
        SocketSendTask sockSendTask = new SocketSendTask();
        sockSendTask.execute(hostIP, "join", username);
    }

    // attempt to join the game
    public void joinGame(View v) {
        String username = ((EditText) findViewById(R.id.username_input)).getText().toString();
        String method = "POST";
        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("action", "join");
        } catch(JSONException je) {
            Log.d("JSON ERROR", je.toString());
        }

        String s = json.toString();
        HttpRequestTask tsk = new HttpRequestTask();
        try {
            JSONObject o = tsk.execute(s, method).get();//o.get("name")
            //o is the jsonobject we get from HttpRequest. If you want to access its element, use o.get("element")
            String temp = "123456";//just for debug. Meaningless.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
