package com.project.cse5473.mafia;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//new class needed for HTTP request. Using asynctask class.
class HttpPollTask extends AsyncTask<String, Void, JSONObject>
{
    URL url;
    HttpURLConnection con;
    public HttpPollTask() {
        super();
    }

    @Override
    protected JSONObject doInBackground(String...pParams) { //[0]: ip address, [1]: json message
        try{
            // setup connection
            String urlStr = "http://" + pParams[0] + ":8000";// the url of localhost
            url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
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
            JSONObject obj;
            try{
                obj = new JSONObject(line);
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
    }
}
