package com.project.cse5473.mafia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addListenerOnSpinnerItemSelection();
        addListenerOnButton();
        submit.setEnabled(false);
        //Need to enable button (vote_butn.setEnabled = true) once server state equals 2
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
}
