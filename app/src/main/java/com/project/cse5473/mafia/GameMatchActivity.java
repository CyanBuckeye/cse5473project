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

/**
 * Created by Ryan on 4/17/2017.
 */

public class GameMatchActivity extends AppCompatActivity {

    Spinner accuse;
    Spinner vote;
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter2;
    ArrayList<String> array;
    HashMap<String, String> playerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        //SET_DAEMON
        //Create two array of players by username from HostGameActivity

        //Create all layout widgets
        accuse = (Spinner) findViewById(R.id.spinner_1);
        adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array); //Need to get player username into the adapters
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accuse.setAdapter(adapter1);
        vote = (Spinner) findViewById(R.id.spinner_2);
        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vote.setAdapter(adapter2);

        //DIST_KEYS
        //EXEC_MATCH
    }

    public class SetDaemon {
        //Choose random player from playerDetails
        //Set json "role" attribute as Daemon
        //Daemon will have different view set with a "Possess" spinner
        //Clicking possess temporarily gives daemon the players key for the round
    }

    public class DistKeys {
        //Distributes symmetric key to each player in "key" attribute in json
    }

    public class ExecMatch {
        //Loop while int == 0 (1 means players win 2 means Daemon wins)
        //ExecRound(playerDetails or json)
        //Check if endgame criteria met
        //end loop

        //ExitMatch([0,1,2])
    }

    public class ExecRound {
        //Randomly choose two players encrypt message to player using their keys
        //If daemon chose one of the players send daemon the encrypted message
        //Two players select people to accuse

        //Repopulate second spinner with two accused suspects
        //Tally players votes
        //if (votes for a suspect) > (half of all remaining players)
        //kill suspect, if suspect was daemon return 2
        //else
        //return 1
    }

    public class ExitMatch {
        //If 0 error
        //If 1 set players win on view, daemon loses
        //If 2 set players lose on view, daemon wins
    }
}
