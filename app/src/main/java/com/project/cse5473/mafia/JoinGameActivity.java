package com.project.cse5473.mafia;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.*;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class JoinGameActivity extends AppCompatActivity {

    //DataOutputStream socketWrite = null;

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
        String username = ((EditText) findViewById(R.id.username_input)).getText().toString();
        String method = "GET";
        HttpRequestTask tsk = new HttpRequestTask();
        try {
            JSONObject o = tsk.execute(username, method).get();//o.get("name")
            //o is the jsonobject we get from HttpRequest. If you want to access its element, use o.get("element")
            String temp = "123456";//just for debug. Meaningless.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
