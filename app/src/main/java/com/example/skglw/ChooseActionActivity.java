package com.example.skglw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_action);

        Button payBtn = findViewById(R.id.btnPay);
        Button refillBtn = findViewById(R.id.btnRefill);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(ChooseActionActivity.this,ChooseActionActivity.class);
               // ChooseActionActivity.this.startActivity(intent);
            }
        });

    }
}
