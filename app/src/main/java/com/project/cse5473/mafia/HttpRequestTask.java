package com.project.cse5473.mafia;

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
import android.os.Debug;
import org.json.*;

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
        String urlStr = "http://10.0.2.2:8000";// the url of localhost
        try{
            String method = pParams[1];
            int responseCode;

            switch (method.toLowerCase()) {
                case "get":
                    // add username to query string
                    urlStr += "?username="+pParams[0];

                    // create connection
                    url = new URL(urlStr);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(method);
                    con.connect();
                    responseCode = con.getResponseCode();
                    if(responseCode != 200) throw new IOException("cannot connect to server");

                    // read response
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
                case "post":
                    // setup connection
                    url = new URL(urlStr);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(method);
                    String jsonStr = pParams[0];

                    // write json to post body
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
                    writer.write(jsonStr);
                    writer.flush();
                    writer.close();

                    // establish connection
                    con.connect();
                    responseCode = con.getResponseCode();
                    if(responseCode != 200) throw new IOException("cannot connect to server");

                    return null;
                default:
                    return null;
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(JSONObject o) {
        super.onPostExecute(o);
    }
}
