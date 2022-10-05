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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingResult extends AppCompatActivity {

    TextView textResultMotionName, textExpectedPracticeTime,textResultPracticeTime, textResultAccuracy;
    Button btnPracticeAgain, btnStoreData, btnBackToMotionList, btnNextMotion, btnBackToMenu;
    String motionName;
    int practiceTime;
    double accuracy = 0.0;
    float[] accuracyList;
    private DatabaseReference DB_ref;
    String jsonF, jsonA;
    ArrayList<Float> inputF = new ArrayList<>();
    ArrayList<Float> inputA = new ArrayList<>();
    boolean normal_end;
    double frontCount = 0.0;
    double stepCount = 0.0;
    double hold_sword_count = 0.0;
    double abdominalCount = 0.0;

    public ArrayList<Float> F_avg;
    public ArrayList<Float> delta_theta;
    public String timestamp_str;
    public MotionAnalysis MA;

    ///菜單相關
    ArrayList<String> menu_motion_arraylist;
    Boolean from_menu;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Ref_WearOSRequest = database.getReference().child("WearOSRequest");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);

        Intent i = getIntent();
        motionName = i.getStringExtra("motionName");
        practiceTime = i.getIntExtra("practiceTime", 0);
        accuracyList = i.getFloatArrayExtra("accuracyList");
        normal_end = i.getBooleanExtra("normal_end", true);
        timestamp_str = i.getStringExtra("time_start");
        Log.d("time_start", "" + timestamp_str);

        menu_motion_arraylist = i.getStringArrayListExtra("menu_motion_arraylist");
        from_menu = i.getBooleanExtra("from_menu", false);


        textResultMotionName = findViewById(R.id.textResultMotionName);
        textExpectedPracticeTime = findViewById(R.id.textExpectedPracticeTime);
        textResultPracticeTime = findViewById(R.id.textResultPracticeTime);
        textResultAccuracy = findViewById(R.id.textResultAccuracy);
        btnPracticeAgain = findViewById(R.id.btnPracticeAgain);
        btnStoreData = findViewById(R.id.btnStoreData);
        btnBackToMotionList = findViewById(R.id.btnBackToMotionList);
        btnNextMotion = findViewById(R.id.btnNextMotion);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        MA = new MotionAnalysis();

        accuracy = 0.0;
        for (int j = 0; j < accuracyList.length; j++) {
            if (accuracyList[j] >= 0.5) {
                accuracy += (float) (1.0 / accuracyList.length);
            }
        }

        //Log.d("accuracyList", Arrays.toString(accuracyList));
        switch (motionName) {
            case "正面劈刀":
            case "Men Uchi":
                frontCount = i.getDoubleExtra("frontCount", 0);
                textResultMotionName.setText(motionName);
                textExpectedPracticeTime.setText(getResources().getString(R.string.expectedPracticeTime) + "：" + practiceTime + getResources().getString(R.string.times));
                textResultPracticeTime.setText(getResources().getString(R.string.practicedTime) + "：" + String.format("%.0f",Math.floor(frontCount)) + getResources().getString(R.string.times));
                textResultAccuracy.setText(getResources().getString(R.string.accuracy) + "：" + String.format("%.2f", accuracy * 100) + "%");
                break;

            case "擦足":
            case "Suri Ashi":
                stepCount = i.getDoubleExtra("stepCount", 0);
                textResultMotionName.setText(motionName);
                textExpectedPracticeTime.setText(getResources().getString(R.string.expectedPracticeTime) + "：" + practiceTime + getResources().getString(R.string.times));
                textResultPracticeTime.setText(getResources().getString(R.string.practicedTime) + "：" + String.format("%.0f",Math.floor(stepCount)) + getResources().getString(R.string.times));
                textResultAccuracy.setText(getResources().getString(R.string.accuracy) + "：" + String.format("%.2f", accuracy * 100) + "%");
                break;

            case "托刀":
            case "Waki Kiamae":
                hold_sword_count = i.getDoubleExtra("hold_sword_count", 0);
                textResultMotionName.setText(motionName);
                textExpectedPracticeTime.setText(getResources().getString(R.string.expectedPracticeTimeS) + "：" + practiceTime + getResources().getString(R.string.seconds));
                textResultPracticeTime.setText(getResources().getString(R.string.practicedTimeS) + "：" +  String.format("%.0f", Math.floor(hold_sword_count)) + getResources().getString(R.string.seconds));
                textResultAccuracy.setText("");
                break;

            case "右胴劈刀":
            case "Dou Uchi":
                abdominalCount = i.getDoubleExtra("abdominalCount", 0);
                textResultMotionName.setText(motionName);
                textExpectedPracticeTime.setText(getResources().getString(R.string.expectedPracticeTime) + "："+ practiceTime + getResources().getString(R.string.times));
                textResultPracticeTime.setText(getResources().getString(R.string.practicedTime) + "：" + String.format("%.0f", Math.floor(abdominalCount)) + getResources().getString(R.string.times));
                textResultAccuracy.setText(getResources().getString(R.string.accuracy) + "：" + String.format("%.2f", accuracy * 100) + "%");
                break;
        }
        /*
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
         */

        if(from_menu){ //從菜單來的
            btnPracticeAgain.setVisibility(View.INVISIBLE);
            Long datetime = System.currentTimeMillis();
            Timestamp timestamp = new Timestamp(datetime);
            String timestamp_str = timestamp.toString();
            //Log.d("timestamp_str", timestamp_str);

            if(menu_motion_arraylist.isEmpty()){ //已完成菜單的所有動作
                btnNextMotion.setVisibility(View.INVISIBLE);
                btnBackToMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(TrainingResult.this, TrainingMenu.class);
                        startActivity(i);
                        TrainingResult.this.finish();
                    }
                });
            }else{ //菜單的動作還沒做完
                btnBackToMenu.setVisibility(View.INVISIBLE);
                btnNextMotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] parts = menu_motion_arraylist.get(0).split(":");

                        Intent i = new Intent(TrainingResult.this, TrainingView.class);
                        i.putExtra("motionName", parts[0]);
                        i.putExtra("practiceTime", Integer.valueOf(parts[1]));
                        i.putExtra("camera_back", false);
                        i.putExtra("time_start", timestamp_str);
                        i.putExtra("menu_motion_arraylist", menu_motion_arraylist);
                        i.putExtra("from_menu", true);
                        startActivity(i);
                        TrainingResult.this.finish();
                    }
                });
            }
            btnBackToMotionList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TrainingResult.this, MotionList.class);
                    startActivity(i);
                    TrainingResult.this.finish();
                }
            });
        }else{
            btnNextMotion.setVisibility(View.INVISIBLE);
            btnBackToMenu.setVisibility(View.INVISIBLE);

            Long datetime = System.currentTimeMillis();
            Timestamp timestamp = new Timestamp(datetime);
            String timestamp_str = timestamp.toString();
            //Log.d("timestamp_str", timestamp_str);

            btnPracticeAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TrainingResult.this, TrainingView.class);
                    i.putExtra("motionName", motionName);
                    i.putExtra("practiceTime", practiceTime);
                    i.putExtra("time_start", timestamp_str);
                    i.putExtra("camera_back", false);
                    startActivity(i);
                    TrainingResult.this.finish();
                }
            });
            btnBackToMotionList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { TrainingResult.this.finish(); }
            });
        }




        Ref_WearOSRequest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                switch (motionName){
                    case "正面劈刀":
                    case "Men Uchi":
                        MA.getData();
                        Toast.makeText(TrainingResult.this, getResources().getString(R.string.watchDataAnalyzed), Toast.LENGTH_SHORT).show();
                        break;
                    case "右胴劈刀":
                    case "Dou Uchi":
                        MA.getData();
                        Toast.makeText(TrainingResult.this, getResources().getString(R.string.watchDataAnalyzed), Toast.LENGTH_SHORT).show();
                        break;
                }

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
        /*
        Ref_WearOSRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MotionAnalysis MA = new MotionAnalysis();
                MA.getData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        //讀手錶分析完的資料
        /*
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
         */
        btnStoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryDataModel result = new HistoryDataModel(timestamp_str, motionName, "",  "",  (float) accuracy, practiceTime);
                DAOHistoryDataModel dao = new DAOHistoryDataModel();
                switch (motionName) {
                    case ("正面劈刀"):
                    case ("Men Uchi"):
                        F_avg = MA.F_avg;
                        delta_theta = MA.delta_theta;
                        result = new HistoryDataModel(timestamp_str, motionName, F_avg.toString(), delta_theta.toString(), (float) accuracy, practiceTime);
                        break;
                    case ("擦足"):
                    case ("Suri Ashi"):
                        result = new HistoryDataModel(timestamp_str, motionName, "", "", (float) accuracy, practiceTime);
                        break;
                    case ("托刀"):
                    case ("Waki Kiamae"):
                        result = new HistoryDataModel(timestamp_str, motionName, "", "", (float) 1.0, practiceTime);
                        break;
                    case ("右胴劈刀"):
                    case ("Dou Uchi"):
                        F_avg = MA.F_avg;
                        delta_theta = MA.delta_theta;
                        result = new HistoryDataModel(timestamp_str, motionName, F_avg.toString(), delta_theta.toString(), (float) accuracy, practiceTime);
                        break;
                }
                dao.add(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(TrainingResult.this, getResources().getString(R.string.saveDataSuccess), Toast.LENGTH_SHORT).show();
                        //Log.d("send", "success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TrainingResult.this, getResources().getString(R.string.saveDataFail) + e.getMessage(), Toast.LENGTH_SHORT).show();
                        //Log.d("send", "false");
                    }
                });
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