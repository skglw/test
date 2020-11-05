package com.example.skglw;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import static android.content.Context.MODE_PRIVATE;

public class Fragment1 extends Fragment{

    // newInstance constructor for creating fragment with arguments
    public static Fragment1 newInstance(/*int page, String title*/) {
        Fragment1 fragmentFirst = new Fragment1();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // executePost("http://mobile-api.fxnode.ru:18888/","");
    }

    private void sendGet() throws Exception {

        String url = "http://mobile-api.fxnode.ru:18888";
        Log.e("AAAAAAAAAAAFFFFF", "00000000");

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        Log.e("AAAAAAAAAAAFFFFF", "1111111");
        //optional default is GET
        con.setRequestMethod("GET");
        Log.e("AAAAAAAAAAAFFFFF", "22222f");

        //add request header
        //   con.setRequestProperty("bankomats");

        int responseCode = con.getResponseCode();
        Log.e("AAAAAAAAAAAFFFFF", "n33333");
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        Log.e("AAAAAAAAAAAFFFFF", String.valueOf(responseCode));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    EditText etText;
    Button btnSave, btnLoad, btnSend,btnPost;

    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_text";

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        etText = view.findViewById(R.id.text);
        btnSave = view.findViewById(R.id.save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPref = getActivity().getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(SAVED_TEXT, etText.getText().toString());
                ed.commit();

            }
        });
        btnLoad = view.findViewById(R.id.load);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPref = getActivity().getPreferences(MODE_PRIVATE);
                String savedText = sPref.getString(SAVED_TEXT, "");
                etText.setText(savedText);
            }
        });
        btnSend= view.findViewById(R.id.send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //создаю новый поток для запроса
                Thread request = new Thread(new Runnable()
                {
                    //прописываю что ему делать
                    public void run()
                    {
                        try{
                            //Log.e("HTTP-get", "try1");
                            URL url = new URL("http://mobile-api.fxnode.ru:18888/bankomats");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            //Log.e("HTTP-get", "try2");
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                //Если запрос выполнен удачно, читаем полученные данные и далее, делаем что-то
                                // Log.e("HTTP-resp", "ok");
                               BufferedReader input = new BufferedReader(
                                        new InputStreamReader(connection.getInputStream(), "UTF-8"));
                                //Log.e("HTTP-GET", "try3");
                                StringBuilder strB = new StringBuilder();
                                String str;
                                while (null != (str = input.readLine())) {
                                   // Log.e("!!","reading");
                                    strB.append(str).append("\r\n");
                                }
                                input.close();
                                Log.e("RESPONSE", String.valueOf(strB));
                                //} catch (IOException e) { }
                            }
                            else {
                               // Log.e("resp", "response not ok");
                            }


                            connection.disconnect();
                        } catch (Exception e) {}//Log.e("HTTP-GET", e.toString());  }
                    }
                });
                request.start();	//Запуск потока
                try {
                    request.join();
                    //Log.e("!!","Главный поток завершён...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        btnPost= view.findViewById(R.id.post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpThread post = new  httpThread();
                Thread myThready = new Thread(post);
               // myThready.start();
            }
        });

        return view;
    }
    class  httpThread		//Нечто, реализующее интерфейс Runnable
            implements Runnable		//(содержащее метод run())
    {
        public void run()		//Этот метод будет выполняться в побочном потоке
        {
            try {
                // Defined URL  where to send data
                URL url = new URL("http://mobile-api.fxnode.ru:18888/bankomats");
                // Send POST data request
                //URLConnection connection = (HttpURLConnection) url.openConnection();
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
               // wr.write( data );
                wr.flush();

                // Get the server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + "\n");
                }


                String text = sb.toString();

                conn.disconnect();
                /*
                //Log.e("HTTP-get", "try1");

                //Log.e("HTTP-get", "try2");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //Если запрос выполнен удачно, читаем полученные данные и далее, делаем что-то
                    // Log.e("HTTP-resp", "ok");
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    //Log.e("HTTP-GET", "try3");
                    StringBuilder strB = new StringBuilder();
                    String str;
                    while (null != (str = input.readLine())) {
                        // Log.e("!!","reading");
                        strB.append(str).append("\r\n");
                    }
                    input.close();
                    Log.e("RESPONSE", String.valueOf(strB));
                    //} catch (IOException e) { }
                } else {
                    // Log.e("resp", "response not ok");
                }*/
            } catch (Exception e) {
            }//Log.e("HTTP-GET", e.toString());  }
        }
    }
}
