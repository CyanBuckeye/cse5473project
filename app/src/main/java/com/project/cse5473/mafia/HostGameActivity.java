package com.project.cse5473.mafia;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class HostGameActivity extends AppCompatActivity {
    static String TAG = "HostGameActivity";

    // socket connection
    ServerSocket ss;
    boolean matchOpen = true;

    // layout references
    TextView ipDisplay;
    TextView joinedPlayersDisplay;
    EditText usernameInput;
    Button joinBtn;
    Button beginBtn;
    HashMap<String, String> playerDetails; // map of usernames and their ip addresses

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Host Game");

        playerDetails = new HashMap<String, String>();

        // get references
        ipDisplay = (TextView) findViewById(R.id.ip_display);
        joinedPlayersDisplay = (TextView) findViewById(R.id.joined_players_display);
        joinBtn = (Button) findViewById(R.id.host_player_join_btn);
        beginBtn = (Button) findViewById(R.id.begin_btn);
        usernameInput = (EditText) findViewById(R.id.username_input);

        // set up ip address display
        String ipAddress = wifiIpAddress(this);
        ipDisplay.setText("IP Address: " + ipAddress);

        // set up server socket
        this.startService(new Intent(this, ServerSocketService.class));
    }

    // taken from http://stackoverflow.com/questions/16730711/get-my-wifi-ip-address-android
    protected String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    // goes to join game activity
    public void hostPlayerJoin(View v){
        // make sure username is valid
        String username = usernameInput.getText().toString();
        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        usernameInput.setText("");
        usernameInput.setEnabled(false);

        // add to list of joined users
        String ipAddress = wifiIpAddress(this);
        playerDetails.put(username, ipAddress);
        updatePlayerList();

        // enable begin match button
        joinBtn.setEnabled(false);
        beginBtn.setEnabled(true);
    }

    // goes to join game activity
    public void beginGame(View v){
        matchOpen = false;
    }

    // updates display of joined players
    public void updatePlayerList() {
        List<String> players = new ArrayList<String>(playerDetails.keySet());
        java.util.Collections.sort(players);
        String text = "Joined players: ";
        for (int i = 0; i < players.size(); i++) {
            text += players.get(i);
            text += ", ";
        }
        if (players.size() > 0) {
            text = text.substring(0, text.length() - 2);
        }

        joinedPlayersDisplay.setText(text);
    }

    // modified http://stackoverflow.com/a/35745834
    public class ServerSocketService extends IntentService {
        public ServerSocketService() {
            super("ServerSocketService");
        }
        @Override
        protected void onHandleIntent(Intent intent) {
            final int port = 5473;
            ServerSocket listener = null;
            try {
                Log.d("ServerSocket", "creating socket");
                listener = new ServerSocket(port);
                while (true) {
                    Log.d("ServerSocket", "waiting for client");
                    Socket socket = listener.accept();
                    Log.d("ServerSocket", String.format("client connected from: %s", socket.getRemoteSocketAddress().toString()));
                    DataInputStream dataRead =  new DataInputStream(socket.getInputStream());

                    while (matchOpen) {
                        String username = dataRead.readUTF();
                        Log.d("ServerSocket", username);
                        Toast.makeText(HostGameActivity.this, username, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch(IOException e) {
                Log.d("ServerSocket", e.toString());
            }
        }
    }
}
