package com.project.cse5473.mafia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

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
        // get inputted username
        String username = ((EditText) findViewById(R.id.username_input)).getText().toString();
        String ip = ((EditText) findViewById(R.id.ip_input)).getText().toString();
        if (ip.isEmpty() || ip == null) { // shortcut so don't have to type it in every time
            ip = "192.168.56.1";
        }

        // set up http post request
        String method = "POST";
        JSONObject json = new JSONObject();
        try{
            json.put("state", 1);
            json.put("type", 1);
            json.put("msg", username);
        } catch(JSONException je) {
            throw new RuntimeException(je);
        }
        String jsonStr = json.toString();
        HttpRequestTask tsk = new HttpRequestTask();
        JSONObject o = new JSONObject();
        try{
            o = tsk.execute(ip, jsonStr).get();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException ee) {
            throw new RuntimeException(ee);
        }

        try {
            String resp = o.getString("msg");
            if (!resp.equals("received")) {
                Toast.makeText(this, "Error joining match, please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch(JSONException e) {
            throw new RuntimeException(e);
        }

        // launch game match
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("dest_ip", ip);
        startActivity(intent);
    }
}
