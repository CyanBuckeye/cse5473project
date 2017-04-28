package com.project.cse5473.mafia;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import android.util.DebugUtils;
import java.lang.Exception;
import java.security.GeneralSecurityException;

import android.os.Debug;
import org.json.*;

//new class needed for HTTP request. Using asynctask class.
class HttpRequestTask extends AsyncTask<String, Void, JSONObject> //pParam[0] = ip address, pParam[1] = json string
{
    private Activity caller;

    public HttpRequestTask() {
        super();
    }

    // use to return data to calling class
    public HttpRequestTask(Activity a) {
        caller = a;
    }

    @Override
    protected JSONObject doInBackground(String...pParams) {
        try{
            // setup connection
            String urlStr = "http://" + pParams[0] + ":8000";// the url of localhost
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            // write json to post
            String jsonStr = pParams[1];
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            writer.write(jsonStr);
            writer.flush();
            writer.close();

            // establish connection and send
            con.connect();

            // receive response
            int responseCode = con.getResponseCode();
            if(responseCode != 200) throw new IOException("cannot connect to server");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder inputLine = new StringBuilder();
            String line;
            while((line = in.readLine()) != null) {
                inputLine.append(line);
            }
            in.close();
            line = inputLine.toString();

            // decrypt line
            String decrypted = "";
            try {
                Crypt c = new Crypt();
                decrypted = c.decrypt_string(line);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }



            JSONObject obj;
            try{
                obj = new JSONObject(decrypted);
            }
            catch (Exception e){
                throw new IOException(e);
            }
            return obj;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(JSONObject o) {
        super.onPostExecute(o);
        if (caller != null) {
            try{
                ((GameActivity) caller).handleServerResponse(o);
            } catch (ClassCastException e) {
                Log.d("HttpRequestTask", e.toString());
            }
        }
    }
}
