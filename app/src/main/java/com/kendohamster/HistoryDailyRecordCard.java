package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HistoryDailyRecordCard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<String> action_name = new ArrayList<>();
    private ArrayList<String> start_time  = new ArrayList<>();
    private ArrayList<String> practice_count = new ArrayList<>();
    private ArrayList<String> accuracy_list = new ArrayList<>();
    private ArrayList<Integer> image_list = new ArrayList<>();
    private ArrayList<HistoryDetailsModel> history_details = new ArrayList<>();

    private RecyclerAdapterCard adapter;

    private DatabaseReference DB_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_daily_record_card);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryDailyRecordCard.this));

        //讀firebase results
        DB_ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference objRef = DB_ref.child("HistoryDataModel");
        objRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot taskSnapshot) {
                for(DataSnapshot snapshot: taskSnapshot.getChildren()){
                    addRecords(snapshot);
                }

                adapter = new RecyclerAdapterCard(action_name, start_time, practice_count, accuracy_list, image_list, history_details, HistoryDailyRecordCard.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void addRecords(DataSnapshot snapshot){
        String motionName = snapshot.child("action_name").getValue().toString();
        String timestamp_str = (String) snapshot.child("timestamp").getValue();
        Timestamp timestamp = Timestamp.valueOf(timestamp_str);
        Float accuracy = Float.valueOf(snapshot.child("accuracy").getValue().toString()) * 100;
        int practice_time = Integer.valueOf(snapshot.child("practice_time").getValue().toString());
        String jsonF = snapshot.child("f_avg").getValue().toString();
        String jsonA = snapshot.child("delta_theta").getValue().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        //Log.d("time_history", calendar.toString());
        HistoryDetailsModel historyDetails;

        action_name.add(motionName);
        start_time.add(String.format("開始時間: %02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
        accuracy_list.add(String.format("正確率: %.2f%%", Double.valueOf(accuracy.toString())));

        switch (motionName) {
            case "正面劈刀":
            case ("Men Uchi"):
                practice_count.add(String.format("練習次數: %d次", practice_time));
                historyDetails = new HistoryDetailsModel(convert_string_to_float(jsonF), convert_string_to_float(jsonA));
                history_details.add(historyDetails);
                image_list.add(R.drawable.hamster1);
                break;
            case "擦足":
            case ("Suri Ashi"):
                practice_count.add(String.format("練習次數: %d次", practice_time));
                historyDetails = new HistoryDetailsModel(null, null);
                history_details.add(historyDetails);
                image_list.add(R.drawable.hamster2);
                break;
            case "托刀":
            case ("Waki Kiamae"):
                practice_count.add(String.format("練習時間: %d秒", practice_time));
                historyDetails = new HistoryDetailsModel(null, null);
                history_details.add(historyDetails);
                image_list.add(R.drawable.hamster3);
                break;
            case ("右胴劈刀"):
            case ("Dou Uchi"):

                break;
        }
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