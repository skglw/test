package com.example.skglw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;

public class RefillActivity extends AppCompatActivity {

    Spinner spFrom, spTo;TextView sendToTv,titleTv;EditText sumEt;TextInputLayout sumTil;
    Button sendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refill);
        sumTil=findViewById(R.id.tilSum);
        sendToTv=findViewById(R.id.tvTo);
        titleTv=findViewById(R.id.tvTitle);
        titleTv.setText("");
        sumEt = findViewById(R.id.etSum);
        ArrayList<String> checkList=new ArrayList<String>();
        for (User.Check check : User.checkList) {
            checkList.add(check.card);
        }
        ArrayList<String> payList=new ArrayList<String>();
        for (User.Pay pay : User.payList) {
           payList.add(pay.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, checkList);
        spFrom = (Spinner) findViewById(R.id.spinnerFrom);
        spTo = (Spinner) findViewById(R.id.spinnerTo);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner

        titleTv.setText("Перевод");

        spFrom.setAdapter(adapter);
        switch(getIntent().getStringExtra("action")){
            case "refillFrom":
                spTo.setAdapter(adapter);
                String card = getIntent().getStringExtra("card");
                int spinnerPosition = adapter.getPosition(card);
                    spFrom.setSelection(spinnerPosition);
                    for (User.Check check : User.checkList) {
                        if (!(card.equals(check.card))) {
                            spTo.setSelection(adapter.getPosition(check.card));
                        }
                    }
                break;
            case "refillTo":
                spTo.setAdapter(adapter);
                 card = getIntent().getStringExtra("card");
                 spinnerPosition = adapter.getPosition(card);
                spTo.setSelection(spinnerPosition);
                for (User.Check check : User.checkList) {
                    if (!(card.equals(check.card))) {
                        spFrom.setSelection(adapter.getPosition(check.card));
                    }
                }
                break;
            case "refill":
                spTo.setAdapter(adapter);
                for (User.Check check : User.checkList) {
                    if (!(spFrom.getSelectedItem().toString().equals(check.card))) {
                        spTo.setSelection(adapter.getPosition(check.card));
                    }
                }
                break;
            case "payTo":
                titleTv.setText("Оплата услуг");
                sendToTv.setText("Оплатить:");
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, payList);
                spTo.setAdapter(adapter);
                spTo.setSelection(adapter.getPosition(getIntent().getStringExtra("name")));
                break;

        }
        sendBtn = findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        sumTil.setErrorEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                if (sumEt.getText().toString().length() == 0) {
                    sumTil.requestFocus();
                    sumTil.setError("Введите сумму перевода");
                }
                else {
                    JSONObject json = new JSONObject();
                    String url = "";
                    Thread request;
                    FutureTask task;
                    if ((getIntent().getStringExtra("action").equals("payTo"))) {
                        String check = "";
                        for (User.Pay pay : User.payList) {
                            if (pay.getName().equals(spTo.getSelectedItem().toString())) {
                                check = pay.getCheck();
                            }
                        }
                        json = new JSONObject();
                        try {
                            json.put("token", User.token);
                            json.put("card_number_sourse", spFrom.getSelectedItem().toString());
                            json.put("number_check", check);
                            json.put("sum", sumEt.getText());
                        } catch (Exception e) {
                        }
                        url = "http://mobile-api.fxnode.ru:18888/pay";

                    } else {
                        json = new JSONObject();
                        try {
                            json.put("token", User.token);
                            json.put("card_number_sourse", spFrom.getSelectedItem().toString());
                            json.put("card_number_dest", spTo.getSelectedItem().toString());
                            json.put("sum", sumEt.getText());
                        } catch (Exception e) {
                        }
                        url = "http://mobile-api.fxnode.ru:18888/refill";
                    }
                    RequestCallable callable = new RequestCallable("POST", url, json, RefillActivity.this);
                    Log.e("u r l >>>", url);
                    task = new FutureTask(callable);
                    request = new Thread(task);
                    request.start();
                    try {
                        request.join();
                        String response = String.valueOf(task.get());
                      //  if (response.contains("error"))
                          //  RequestCallable.errorHandler(response, RefillActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    finish();

                }
               /* JSONObject json = new JSONObject();
                try{
                    json.put("token", User.token);
                    json.put("card_number_sourse", spFrom.getSelectedItem().toString());
                    json.put("card_number_dest", spTo.getSelectedItem().toString());
                    json.put("sum",sumTv.getText());
                }
                catch(Exception e){}
                RequestCallable callable= new RequestCallable("POST", "http://mobile-api.fxnode.ru:18888/refill", json);
                FutureTask task = new FutureTask(callable);
                Thread request = new Thread(task);
                request.start();
                Log.e("REQUEST IS RUNNING", String.valueOf(json));
                try {
                    request.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        });
    }
}
