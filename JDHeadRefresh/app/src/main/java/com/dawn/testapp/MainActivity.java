package com.dawn.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, HomeActivity.class));
                CustomToast.getNewInstance(getApplicationContext());
                CustomToast.show("Hello world!", Toast.LENGTH_LONG);
                break;
            case R.id.btn2:
                startActivity(new Intent(this, Home2Activity.class));
                CustomToast.show("aaaa", Toast.LENGTH_SHORT);
                break;
            case R.id.btn3:
                startActivity(new Intent(this, Home3Activity.class));
                break;
        }
    }
}
