package com.example.skglw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LaunchActivity extends AppCompatActivity {

    final String[] data= new String[100]; final StringBuilder strB = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        Button btnAuth=findViewById(R.id.auth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etUN = findViewById(R.id.username);
                final String username = etUN.getText().toString();
                EditText etPW = findViewById(R.id.password);
                final String password = etPW.getText().toString();

                Thread request = new Thread(new Runnable() {
                    public void run() {
                        try {
                            URL url = new URL("http://mobile-api.fxnode.ru:18888/login");
                            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                         // connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        //  connection.setRequestProperty("Accept","application/json");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);

                            JSONObject json = new JSONObject();
                            json.put("username", username);
                            json.put("password", password);

                        //  Log.e("JSON", json.toString());
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
                });

                request.start();
                try {
                    request.join();
                    String jsonString = String.valueOf(strB);
                    JSONObject obj = new JSONObject(jsonString);
                    String token = obj.get("token").toString();
                    Log.e("RESPONSE>>", jsonString);
                    Log.e("GOT TOKEN>>", token);

                    Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                    intent.putExtra("login", username);
                    intent.putExtra("token", token);
                 // intent.putExtra("password", password);
                    LaunchActivity.this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
