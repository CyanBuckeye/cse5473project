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
        tsk.execute(ip, jsonStr);

        // launch game match
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("username", username);
        if (ip == "") ip = "10.0.2.2";
        intent.putExtra("dest_ip", ip);
        startActivity(intent);
    }
}
