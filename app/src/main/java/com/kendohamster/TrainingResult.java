package com.kendohamster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class TrainingResult extends AppCompatActivity {

    TextView textResultMotionName, textResultPracticeTime, textResultAccuracy;
    Button btnPracticeAgain, btnDownloadVideo, btnBackToMotionList;
    String motionName;
    int practiceTime;
    double accuracy = 0.0;
    float[] accuracyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);

        Intent i = getIntent();
        motionName = i.getStringExtra("motionName");
        practiceTime = i.getIntExtra("practiceTime", 0);
        accuracyList = i.getFloatArrayExtra("accuracyList");

        //Log.d("accuracyList", Arrays.toString(accuracyList));
        accuracy = 0.0;
        for(int j = 0; j < accuracyList.length; j++){
            if(accuracyList[j] >= 0.6){
                accuracy += (float)(1.0 / accuracyList.length);
            }
        }

        textResultMotionName = findViewById(R.id.textResultMotionName);
        textResultPracticeTime = findViewById(R.id.textResultPracticeTime);
        textResultAccuracy = findViewById(R.id.textResultAccuracy);
        btnPracticeAgain = findViewById(R.id.btnPracticeAgain);
        btnDownloadVideo = findViewById(R.id.btnDownloadVideo);
        btnBackToMotionList = findViewById(R.id.btnBackToMotionList);

        textResultMotionName.setText(motionName);
        textResultPracticeTime.setText("練習次數：" + String.valueOf(practiceTime) + "次");
        textResultAccuracy.setText("正確率：" + String.format("%.2f", accuracy*100) + "%");
        Log.d("accuracy", String.format("%.2f", accuracy*100));

        btnPracticeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TrainingResult.this, TrainingView.class);
                i.putExtra("motionName", motionName);
                i.putExtra("practiceTime", practiceTime);
                startActivity(i);
                TrainingResult.this.finish();
            }
        });
        btnDownloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Download Video",Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToMotionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrainingResult.this.finish();
            }
        });

    }
}