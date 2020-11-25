package com.example.skglw;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class RequestCallable implements Callable {
    static String message="";
    static Activity activity=null;
    String method = "GET";
    URL url;
    JSONObject json;
    final StringBuilder strB = new StringBuilder();

    public RequestCallable(String method, String url, JSONObject json) {
        this.activity = null;
        this.method = method;
        try{this.url = new URL( url);}catch(Exception e){}
        this.json = json;
    }

    public RequestCallable(String method, String url, JSONObject json, Activity activity) {
        this.activity = activity;
        this.method = method;
        try{this.url = new URL( url);}catch(Exception e){}
        this.json = json;
    }
    public RequestCallable( String url, Activity activity) {
        this.activity = activity;
        this.method = "GET";
        try{this.url = new URL( url);}catch(Exception e){}
    }
    public RequestCallable( String url) {
        this.activity = null;
        this.method = "GET";
        try{this.url = new URL( url);}catch(Exception e){}
    }

    public String call() {
        try {
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            // connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //  connection.setRequestProperty("Accept","application/json");
            switch (method){
                case "GET":
                    break;
                case "POST":
                case "PUT":
                case "DELETE":
                    connection.setDoOutput(true);
                    DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                    output.writeBytes(json.toString());//(URLEncoder.encode(jsonParam.toString(), "UTF-8"));

                    output.flush();
                    output.close();
                    break;


            }

           /* connection.setDoInput(true);

            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(json.toString());//(URLEncoder.encode(jsonParam.toString(), "UTF-8"));

            output.flush();
            output.close();*/
            Log.e("json>>", String.valueOf(json));
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
                //if (activity!=null)
                try{
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Операция выполнена", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception e){}
              //  if(context!=null)Toast.makeText(context, "Операция выполнена",Toast.LENGTH_LONG).show();
            } else {
                Log.e("HTTP-ERROR", "response code is not ok");

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                String str; //int i = 0;
                while (null != (str = input.readLine())) {
                    strB.append(str);
                }
                input.close();
                Log.e("HTTP-ERROR", strB.toString());
                errorHandler(strB.toString());
            }
            connection.disconnect();
        } catch (Exception e) {Log.e("HTTP-ERROR", e.toString()); }



        return  String.valueOf(strB);
    }

    public static void errorHandler(String response) {
        message = response;
        if (response.contains("blocked")) {
            message = "Карта с номером " + response.replaceAll("\\D+", "") + " заблокирована.";
        } else if (response.contains("required")) {
            message = "Номер счета " + response.replaceAll("\\D+", "") + " не действителен.";
        } else if (response.contains("not enough")) {
            message = "На счете недостаточно средств для совершения операции.";
        } else if (response.contains("found")) {
            message = "Cчет с номером " + response.replaceAll("\\D+", "") + " не найден.";
        }
        message += "Перевод отменен.";
        if (activity!=null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }




}