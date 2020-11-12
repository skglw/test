package com.example.skglw;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestRunnable implements Runnable {
 String method = "GET"; URL url; JSONObject json; final StringBuilder strB = new StringBuilder();

    public RequestRunnable(String method, String url, JSONObject json) {
            this.method = method;
            try{this.url = new URL( url);}catch(Exception e){}
            this.json = json;
        }

        public void run() {
            try {
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            // connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //  connection.setRequestProperty("Accept","application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(json.toString());//(URLEncoder.encode(jsonParam.toString(), "UTF-8"));

            output.flush();
            output.close();

            Log.e("STATUS>>", String.valueOf(connection.getResponseCode()));
            Log.e("MSG>>" , connection.getResponseMessage());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str; //int i = 0;
                while (null != (str = input.readLine())) {
                    strB.append(str);
                }
                input.close();
            } else {
                Log.e("HTTP-ERROR", "response code is not ok");
            }
            connection.disconnect();
        } catch (Exception e) {Log.e("HTTP-ERROR", e.toString()); }
    }


}

