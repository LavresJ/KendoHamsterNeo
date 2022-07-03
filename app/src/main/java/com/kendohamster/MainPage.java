package com.kendohamster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainPage extends AppCompatActivity implements View.OnClickListener{

    Button btnMotionList,btnHistory,btnTrainingMenu,btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        btnMotionList = findViewById(R.id.btnMotionList);
        btnHistory = findViewById(R.id.btnHistory);
        btnTrainingMenu =findViewById(R.id.btnTrainingMenu);
        btnSettings = findViewById(R.id.btnSettings);

        btnMotionList.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnTrainingMenu.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.btnMotionList:
                i = new Intent(this,MotionList.class);
                break;
            case R.id.btnHistory:
                i = new Intent(this, History.class);
                break;
            case R.id.btnTrainingMenu:
                i = new Intent(this, TrainingMenu.class);
                break;
            case R.id.btnSettings:
                i = new Intent(this, Settings.class);
                break;
            default:
                break;
        }
        startActivity(i);
    }
}