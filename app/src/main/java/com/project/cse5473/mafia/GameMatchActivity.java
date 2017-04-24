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
import android.widget.Spinner;
import android.widget.ArrayAdapter;

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
import java.util.Set;
import java.util.Random;
import java.lang.Thread;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 * Created by Ryan on 4/17/2017.
 */

public class GameMatchActivity extends AppCompatActivity {

    TextView RoundDisplay;
    TextView EndDisplay;
    Spinner accuse, vote, possess, frame;
    ArrayAdapter<String> adapter1, adapter2, adapter3, adapter4;

    Intent intent = getIntent();
    HashMap<String, String> playerDetails  = (HashMap<String, String>)intent.getSerializableExtra("players"); //Players usernames and their cooresponding ip addresses
    Set<String> useTemp = playerDetails.keySet(); //Players left in play
    ArrayList<String> users = new ArrayList<String>(useTemp); //List of all players usernames, list isn't changed in order to have a unique int id representing each player
    ArrayList<String> victims = users; //Daemons list of players that can be possessed
    ArrayList<String> suspects = new ArrayList<String>(3); //Players suspected of being the daemon for the round
    int endgame_status; //[0] in-process, [1] players win, [2] daemon wins
    int daemon_id; //Int in ArrayList "users" representing the daemon
    int round = 0;

    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        //Need to create JSON object with properties cooresponding to ip address
        player = new Player();
        player.name = "P1";//From playerDetails
        player.ip = "127.0.0.15"; //From playerDetails
        player.id = 1;//Need to implement in place of users ArrayList CHANGE NEEDED
        player.role = "player1";//default will be changed for the daemon
        player.possessed = false;//default will be changeed depending on who daemon possesses each round
        player.killed = false; //Change players state when they get killed
        player.votes = 0;

        //SET_DAEMON
        daemon_id = SetDaemon();
        //Create list of possessable victims
        victims.remove(daemon_id);
        //Set round
        RoundDisplay = (TextView) findViewById(R.id.round_num);

        //Set daemon view
        if(player.role == "daemon"){
            //Create all layout widgets for the daemon
            setContentView(R.layout.activity_daemon);
            possess = (Spinner) findViewById(R.id.spinner_3);
            adapter3 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, victims); //Need to get player username into the adapters
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            possess.setAdapter(adapter3);

            frame = (Spinner) findViewById(R.id.spinner_4);
            adapter4 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, users); //Need to get player username into the adapters
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            frame.setAdapter(adapter4);
            ((Spinner) accuse).getSelectedView().setEnabled(false);
            frame.setEnabled(false);
        }
        //Set player view
        else {
            //Create all layout widgets for normal players
            accuse = (Spinner) findViewById(R.id.spinner_1);
            adapter1 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, users); //Need to get player username into the adapters
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accuse.setAdapter(adapter1);
            ((Spinner) accuse).getSelectedView().setEnabled(false);
            accuse.setEnabled(false);

            vote = (Spinner) findViewById(R.id.spinner_2);
            adapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, suspects);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vote.setAdapter(adapter2);
        }

        //DIST_KEYS, might not be needed
        //EXEC_MATCH
        ExecMatch(endgame_status);
    }

    public int SetDaemon () {
        //Choose random player from available users
        Random rand = new Random();
        int id = rand.nextInt(users.size()) + 1;
        //Set json "role" attribute as Daemon
        player.role = "daemon";
        //Daemon will have different view set with a "Possess" spinner
        //Clicking possess temporarily gives daemon the players key for the round
        //This is handled in onCreate()

        //Return id in ArrayList "users" of the daemon
        return id;
    }

    public class DistKeys { //Might not need
        //Distributes symmetric key to each player in "key" attribute in json
    }

    public void ExecMatch(int endgame_status) {
        //Loop while int == 0 (1 means players win 2 means Daemon wins)
        while(endgame_status == 0){
            //ExecRound(playerDetails or json)
            endgame_status = ExecRound();
            //end loop and check if endgame criteria met
        }

        //ExitMatch([0,1,2])
        ExitMatch(endgame_status);
    }

    public int ExecRound (){
        int accusser1, accusser2;
        String accussed1 = "";
        String accussed2 = "";
        String possessed_player = "";
        boolean set = false;

        round = round++;
        RoundDisplay = (TextView) findViewById(R.id.round_num);
        RoundDisplay.setText("Round " + round);

        //Randomly choose two players encrypt message to player using their keys
        Random rand = new Random();
        accusser1 = rand.nextInt(users.size()) + 1;
        rand = new Random();
        accusser2 = rand.nextInt(users.size()) + 1;
        while(accusser1 == accusser2){
            rand = new Random();
            accusser2 = rand.nextInt(users.size()) + 1;
        }

        //Daemon possesses a player
        if(player.role == "daemon"){
            possess = (Spinner)findViewById(R.id.spinner_3);
            possessed_player = possess.getSelectedItem().toString();
            set = true;
        }

        //Waits for daemon to select possessed player
        while (set == false){ //ERROR PRONE
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //If daemon chose one of the players send daemon the encrypted message
        //Check choice1 and choice2 id at users, if either one has a possessed state,
        //set daemon frame spinner to active and possessed user accuse spinner to inactive
        //Two players select people to accuse
        if(possessed_player.equals(users.get(accusser1))){
            //Depending on how encryption is done do something here
            frame.setEnabled(true);
            accussed1 = frame.getSelectedItem().toString();
            if(player.name == users.get(accusser2)){
                accuse.setEnabled(true);
                accussed2 = accuse.getSelectedItem().toString();
            }
        }
        else if(possessed_player.equals(users.get(accusser2))){
            frame.setEnabled(true);
            accussed1 = frame.getSelectedItem().toString();
            if(player.name == users.get(accusser1)){
                accuse.setEnabled(true);
                accussed2 = accuse.getSelectedItem().toString();
            }
        }
        else{
            if(player.name == users.get(accusser1)){
                accuse.setEnabled(true);
                accussed1 = accuse.getSelectedItem().toString();
            }
            if(player.name == users.get(accusser2)){
                accuse.setEnabled(true);
                accussed2 = accuse.getSelectedItem().toString();
            }
        }


        //Repopulate second spinner with two accused suspects
        suspects.add(accussed1);
        suspects.add(accussed2);
        suspects.add("ABSTAIN");
        //Tally players votes, not quite sure how to do this


        //if (votes for a suspect) > (half of all remaining players)
        //kill suspect, if suspect was daemon return 2
       // if(){

       // }
        //else
        //return 1
       // else{
            //add killing innocent player
       //     return 1;
       // }
        return 0;
    }

    public void ExitMatch (int endgame_status) {
        //If 1 set players win on view, daemon loses
        if(endgame_status == 1){
            EndDisplay = (TextView) findViewById(R.id.Win_Lose_Text);
            if(player.role == "player"){
                EndDisplay.setText("YOU WIN!!!");
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
            else if(player.role == "daemon"){
                EndDisplay.setText("YOU LOSE!!!");
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        }
        //If 2 set players lose on view, daemon wins
        else if(endgame_status == 2){
            EndDisplay = (TextView) findViewById(R.id.Win_Lose_Text);
            if(player.role == "player"){
                EndDisplay.setText("YOU LOSE!!!");
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
            else if(player.role == "daemon"){
                EndDisplay.setText("YOU WIN!!!");
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        }
        //If some other int error out
        else{
            EndDisplay = (TextView) findViewById(R.id.Win_Lose_Text);
            EndDisplay.setText("ERROR!!!");
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
