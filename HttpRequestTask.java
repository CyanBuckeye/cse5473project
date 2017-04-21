package com.project.cse5473.mafia;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import android.util.DebugUtils;
import java.lang.Exception;
import android.os.Debug;
import org.json.*;
/**
 * Created by Administrator on 2017/4/20.
 */

//new class needed for HTTP request. Using asynctask class.
class HttpRequestTask extends AsyncTask<String, String, JSONObject>//the second parameter stands for the HTTP request, such as GET or POST,
    // the first one stands for the message you want to transfer to the server; the third one is type of returning value
{
    URL url;
    HttpURLConnection con;
    public HttpRequestTask() {
        super();
    }

    @Override
    protected JSONObject doInBackground(String...pParams) {
        android.os.Debug.waitForDebugger();//for debug
        String str = "http://10.0.2.2:8000";// the url of localhost
        try{
            url = new URL(str);
            con = (HttpURLConnection) url.openConnection();
            String method = pParams[1];
            String msg = pParams[0];
            con.setRequestMethod(method);
            con.connect();
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
