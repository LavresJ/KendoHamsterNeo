package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingResult extends AppCompatActivity {

    TextView textResultMotionName, textResultPracticeTime, textResultAccuracy;
    Button btnPracticeAgain, btnDownloadVideo, btnBackToMotionList;
    String motionName;
    int practiceTime;
    double accuracy = 0.0;
    float[] accuracyList;
    private DatabaseReference DB_ref;
    String jsonF, jsonA;
    ArrayList<Float> inputF = new ArrayList<>();
    ArrayList<Float> inputA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);

        Intent i = getIntent();
        motionName = i.getStringExtra("motionName");
        practiceTime = i.getIntExtra("practiceTime", 0);
        accuracyList = i.getFloatArrayExtra("accuracyList");

        textResultMotionName = findViewById(R.id.textResultMotionName);
        textResultPracticeTime = findViewById(R.id.textResultPracticeTime);
        textResultAccuracy = findViewById(R.id.textResultAccuracy);
        btnPracticeAgain = findViewById(R.id.btnPracticeAgain);
        btnDownloadVideo = findViewById(R.id.btnDownloadVideo);
        btnBackToMotionList = findViewById(R.id.btnBackToMotionList);


        //Log.d("accuracyList", Arrays.toString(accuracyList));
        if(accuracyList.length != 0) { //是動態動作
            accuracy = 0.0;
            for (int j = 0; j < accuracyList.length; j++) {
                if (accuracyList[j] >= 0.6) {
                    accuracy += (float) (1.0 / accuracyList.length);
                }
            }
            textResultMotionName.setText(motionName);
            textResultPracticeTime.setText("練習次數：" + String.valueOf(practiceTime) + "次");
            textResultAccuracy.setText("正確率：" + String.format("%.2f", accuracy*100) + "%");
            Log.d("accuracy", String.format("%.2f", accuracy*100));
        }else{ //是靜態動作
            textResultMotionName.setText(motionName);
            textResultPracticeTime.setText("練習時間：" + String.valueOf(practiceTime) + "秒");
            textResultAccuracy.setText("");
        }

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

        //讀手錶分析完的資料
        DB_ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference objRef = DB_ref.child("WatchResultModel");
        objRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                jsonF = snapshot.child("f_avg").getValue().toString();
                jsonA = snapshot.child("delta_theta").getValue().toString();
                inputF = convert_string_to_float(jsonF);
                inputA = convert_string_to_float(jsonA);

                Log.d("F_avg", inputF.toString());
                Log.d("Angle", inputA.toString());

                snapshot.getRef().removeValue();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<Float> convert_string_to_float(String json){
        ArrayList<Float> input = new ArrayList<Float>();
        String jsonD; //去除json字串的 "[", "]"
        String[] jsonString; //將json字串分割儲存成字串陣列

        jsonD = json.substring(1, json.length()-1);
        jsonString = jsonD.split(", ");

        //將string陣列轉成ArrayList
        List<String> jsonStringList =  Arrays.asList(jsonString);
        ArrayList<String> jsonStringArrayList = new ArrayList<String>(jsonStringList);

        for(int i = 0; i < jsonStringArrayList.size(); i++){
            input.add( Float.parseFloat(jsonStringArrayList.get(i)) );
        }

        return input;
    }
}