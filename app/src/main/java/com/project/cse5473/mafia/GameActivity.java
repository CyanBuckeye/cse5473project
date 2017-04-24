package com.project.cse5473.mafia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button submit;
    private Activity context;

    String username, destIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        addListenerOnSpinnerItemSelection();
        addListenerOnButton();
        submit.setEnabled(false);
        //Need to enable button (vote_butn.setEnabled = true) once server state equals 2

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        destIP  = bundle.getString("dest_ip");

        // start polling webserver
        startPolling();
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.vote_spinner);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.vote_spinner);
        submit = (Button) findViewById(R.id.vote_btn);

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String method = "POST";
                JSONObject json = new JSONObject();
                try {
                    json.put("state", 2);
                    json.put("type", 2);
                    json.put("msg", String.valueOf(spinner.getSelectedItem()));
                } catch (JSONException je) {
                    throw new RuntimeException(je);
                }
                String ip = "192.168.56.1";
                String jsonStr = json.toString();
                HttpRequestTask tsk = new HttpRequestTask();
                tsk.execute(ip, jsonStr);
            }

        });
    }

    // start polling the server for updates every 200ms
    private void startPolling() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        final String requestStr = "{\"state\": 0, \"type\": 0, \"msg\": \"" + username + "\"}";
        final String ip = destIP;
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            HttpRequestTask pollTask = new HttpRequestTask(context);
                            pollTask.execute(ip, requestStr);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 200);
    }

    // handle the response from polling the server
    public void handleServerResponse(JSONObject jsonObj) {
        String s = jsonObj.toString();
        int type = -1;
        int state = -1;
        String msg = "";

        try {
            type = jsonObj.getInt("type");
            state = jsonObj.getInt("state");
            msg = jsonObj.getString("msg");

            // joined the game, get list of player names
            if (type == 0) {
                JSONArray players = jsonObj.getJSONArray("msg");
                if (players.length() == 3) {
                    gameStart();
                }
            }

            Log.d("GameActivity", s);
        } catch(JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // called once all three players have joined
    private void gameStart() {
        Log.d("GameActivity", "Everyone joined");
        submit.setEnabled(true);
    }


}
