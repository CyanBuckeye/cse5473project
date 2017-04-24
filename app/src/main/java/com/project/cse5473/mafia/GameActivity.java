package com.project.cse5473.mafia;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    String username;
    String destIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        destIP  = bundle.getString("dest_ip");

        // start polling webserver
        startPolling();
    }

    public void startPolling() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        final String requestStr = "{\"state\": \"\", \"type\":0, \"msg\": \""+ username + "\"}";
        final String ip = destIP;
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            HttpRequestTask pollTask = new HttpRequestTask();
                            pollTask.execute(ip, requestStr);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 200); //execute in every 200 ms
    }
}
