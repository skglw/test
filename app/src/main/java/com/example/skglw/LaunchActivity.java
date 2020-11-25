package com.example.skglw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONObject;

import java.util.concurrent.FutureTask;

public class LaunchActivity extends AppCompatActivity {
    EditText etUN, etPW;
    TextInputLayout tilUN, tilPW;
    TextWatcher textWatcher;
    String login, password, errorMes;
    SharedPreferences preferences;

    static SharedPreferences  mSettings;
    public static final String  APP_PREFERENCES = "app";
    public static final String APP_PREFERENCES_TOKEN = "token";
    public static final String APP_PREFERENCES_LOGIN = "login";
    public static final String APP_PREFERENCES_PASSWORD = "password";
   // final String[] data = new String[100];
   // final StringBuilder strB = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        tilPW = findViewById(R.id.tilPassword);
        etPW = findViewById(R.id.password);
        tilUN = findViewById(R.id.tilUsername);
        etUN = findViewById(R.id.username);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

       // try {
            if(mSettings.contains(APP_PREFERENCES_TOKEN)) {
                User.token = (mSettings.getString(APP_PREFERENCES_TOKEN, ""));
                if(getUser()!=null){
                    User.login = (mSettings.getString(APP_PREFERENCES_LOGIN, ""));
                    User.password = (mSettings.getString(APP_PREFERENCES_PASSWORD, ""));
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    intent.putExtra("token", User.token);
                    intent.putExtra("name", User.username);
                    Log.e("@@\n\n@",User.login+User.password+User.token);
                    LaunchActivity.this.startActivity(intent);
                }
                else {
                    User.token = null;
                    etUN.setText(mSettings.getString(APP_PREFERENCES_LOGIN, ""));
                    etPW.setText(mSettings.getString(APP_PREFERENCES_PASSWORD, ""));
                }
            }

         /*   SharedPreferences sp = getSharedPreferences(MY_SETTINGS,
                    Context.MODE_PRIVATE);
            // проверяем, первый ли раз открывается программа
            boolean hasVisited = sp.getBoolean("hasVisited", false);

            if (!hasVisited) {
                // выводим нужную активность
                Editor e = sp.edit();
                e.putBoolean("hasVisited", true);
                e.commit(); // не забудьте подтвердить изменения
            }

            String token  = preferences.getString("token", null);
            Log.e("TOKEN",token);
            String login = sPref.getString("login", "");
            Log.e("TOKEN",login);
            String password = sPref.getString("password", "");
            Log.e("TOKEN",password);*/

      //  }catch(Exception e){Toast.makeText(this, "fist load",Toast.LENGTH_SHORT);}

        Button btnAuth = findViewById(R.id.auth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login = etUN.getText().toString();
                password = etPW.getText().toString();

                    if (login.length() == 0) {
                        etUN.requestFocus();
                        tilUN.setError("Введите имя пользователя");
                    }
                    if (password.length() == 0) {
                        etPW.requestFocus();
                        tilPW.setError("Введите пароль");
                    }
                    else{
                        User.login = login;
                        User.password = password;
                        //Boolean auth = auth();
                      if(auth()){


                          SharedPreferences.Editor editor = mSettings.edit();
                          editor.putString(APP_PREFERENCES_LOGIN, User.login);
                          editor.putString(APP_PREFERENCES_PASSWORD, User.password);
                          editor.putString(APP_PREFERENCES_TOKEN, User.token);
                          editor.apply();

                        //  Log.e("jbdfugvzsa,edkf>>>", (mSettings.getString(APP_PREFERENCES_TOKEN, "")));
                         // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LaunchActivity.this);
                        /*  SharedPreferences.Editor editor = preferences.edit();
                          editor.putString("login", User.login);
                          editor.putString("token", User.token);
                          editor.putString("password",User.password);
                          editor.commit();*/
                          Log.e("@@\n\n@",User.login+User.password+User.token);
                         /* String token  = preferences.getString("token", null);
                          Log.e("TOKEN",token);
                          String login = sPref.getString("login", "");
                          Log.e("TOKEN",login);
                          String password = sPref.getString("password", "");
                          Log.e("TOKEN",password);*/
                          getUser();
                          Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                          intent.putExtra("token", User.token);
                          intent.putExtra("name", User.username);
                        LaunchActivity.this.startActivity(intent);
                      }
                     else {
                          etPW.setText("");
                          tilPW.setError(" ");
                          tilUN.setError(" ");
                          //Toast.makeText(this,"Ошибка авторизации" +message, Toast.LENGTH_SHORT).show();
                          etUN.addTextChangedListener(textWatcher);
                          etPW.addTextChangedListener(textWatcher);
                      }//authFailed(errorMes);
                    }
            }
        });

        Button btnMap = findViewById(R.id.map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPW.setErrorEnabled(false);
                tilUN.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private Boolean auth() {
        JSONObject json = new JSONObject();
        try {
            json.put("username", User.login);
            json.put("password", User.password);
        } catch (Exception e) {
        }
        String response = "";
        Log.e("json", json.toString());
        RequestCallable callable = new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/login", json);
        FutureTask task = new FutureTask(callable);
        Thread request = new Thread(task);
        request.start();
        // Log.e("REQUEST IS RUNNING", String.valueOf(request.isAlive()));
        try {
            request.join();
            response = String.valueOf(task.get());
            // Log.e("response: ", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            User.token = obj.get("token").toString();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    return false;

    }
    private String getUser() {
        JSONObject json = new JSONObject();
        try {
            json.put("token", User.token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestCallable callable = new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/getuser", json);
        FutureTask task = new FutureTask(callable);
        Thread request = new Thread(task);
        request.start();

        try {
            request.join();
            String jsonString = String.valueOf(task.get());
            json = new JSONObject(jsonString);
            String firstname = " " + json.get("firstname").toString();
            String lastname = json.get("lastname").toString();
            String middlename = " " + json.get("middlename").toString();
            String name = lastname + firstname + middlename;
            User.username = name;
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void authFailed(String message) {
        etUN.addTextChangedListener(textWatcher);
        etPW.addTextChangedListener(textWatcher);
        etPW.setText("");
        tilPW.setError(" ");
        tilUN.setError(" ");
        Toast.makeText(this,"Ошибка авторизации" +message, Toast.LENGTH_SHORT).show();

      //  etUN.setText("");
    }
}
