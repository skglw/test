package com.example.skglw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;

public class PayActivity extends AppCompatActivity {
    EditText sendToET, sumEt;
    TextInputLayout sendToTil, sumTil;
    Spinner spFrom;
    TextView  sendToEt,titleTv;
    Button sendBtn,cardBtn, checkBtn;
    TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);
        sendToET = findViewById(R.id.etSendTo);
         sendToTil =findViewById(R.id.tilSendTo);
        sendToEt=findViewById(R.id.etSendTo);
        titleTv=findViewById(R.id.tvTitle);
        titleTv.setText("");
        sumEt = findViewById(R.id.etSum);
        sumTil = findViewById(R.id.tilSum);
        ArrayList<String> checkList=new ArrayList<String>();
        for (User.Check check : User.checkList) {
            checkList.add(check.card);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, checkList);
        spFrom = findViewById(R.id.spinnerFrom);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(adapter);

        titleTv.setText("Перевод");
        cardBtn = findViewById(R.id.btnCard);
        checkBtn = findViewById(R.id.btnCheck);
        cardBtn.setSelected(true);
        sendBtn = findViewById(R.id.btnSend);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sumTil.isErrorEnabled())sumTil.setErrorEnabled(false);
                if (sendToTil.isErrorEnabled())sendToTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sum = sumEt.getText().toString();
                sumEt.addTextChangedListener(textWatcher);
                sendToEt.addTextChangedListener(textWatcher);
                if (sum.length() == 0) {
                    sumTil.requestFocus();
                    sumTil.setError("Введите сумму перевода");
                }
                else if (sendToEt.getText().toString().length() == 0) {
                    sendToTil.requestFocus();
                    sendToTil.setError("Это поле не может быть пустым");
                }
                else {
                    JSONObject json = new JSONObject();
                    String url = "";
                    if (cardBtn.isEnabled()) {
                        try {
                            json.put("token", User.token);
                            json.put("card_number_sourse", spFrom.getSelectedItem().toString());
                            json.put("card_number_dest", sendToET.getText().toString());
                            json.put("sum", sum);
                        } catch (Exception e) {}
                        url = "http://mobile-api.fxnode.ru:18888/refill";
                    } else if (checkBtn.isEnabled()) {
                        json = new JSONObject();
                        try {
                            json.put("token", User.token);
                            json.put("card_number_sourse", spFrom.getSelectedItem().toString());
                            json.put("number_check", sendToET.getText().toString());
                            json.put("sum", sum);
                        } catch (Exception e) { }
                        url = "http://mobile-api.fxnode.ru:18888/pay";
                    }
                    RequestCallable callable = new RequestCallable("POST", url, json, PayActivity.this);
                    FutureTask task = new FutureTask(callable);
                    Thread request = new Thread(task);

                    request.start();
                    Log.e("REQUEST IS RUNNING", String.valueOf(json));
                    try {
                        request.join();
                        String response = String.valueOf(task.get());
                        //if (response.contains("error"))
                           // RequestCallable.errorHandler(response, PayActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });
        MaterialButtonToggleGroup toggleBtn = findViewById(R.id.toggleButton);
        toggleBtn.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId==(R.id.btnCard)) sendToTil.setHint("Номер карты назначения :");
                else sendToTil.setHint("Номер счета назначения :");
            }
        });
        }



}

