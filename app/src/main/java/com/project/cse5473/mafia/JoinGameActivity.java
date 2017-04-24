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

    String username = "";

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
        username = ((EditText) findViewById(R.id.username_input)).getText().toString();

        // set up http post request
        String method = "POST";
        JSONObject json = new JSONObject();
        try{
            json.put("username", username);
            json.put("action", "join");
        } catch(JSONException je) {
            throw new RuntimeException(je);
        }
        String s = json.toString();
        HttpRequestTask tsk = new HttpRequestTask();
        try {
            JSONObject o = tsk.execute(s, method).get();//o.get("name")

            // launch game match
            Intent intent = new Intent(this, GameMatchActivity.class);
            startActivity(intent);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }  catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // poll the http server
    private void poll() {
        HttpRequestTask pollTask = new HttpRequestTask();
        try {
            JSONObject o = pollTask.execute(username, "GET").get();//o.get("name")
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
