package com.kendohamster.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.kendohamster.R;
import com.kendohamster.RecyclerAdapterForHistoryList;

import java.util.ArrayList;

public class HistoryDailyRecord extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapterForHistoryList adapter;

    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<String> motionsList = new ArrayList<>();
    private  ArrayList<Integer> countList = new ArrayList<>();
    private  ArrayList<Double> correctRateList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_daily_record);

        setTitle("2022/03/09");


        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryDailyRecord.this));

        timeList.add("20:01");
        timeList.add("20:04");
        timeList.add("20:13");
        timeList.add("20:18");
        timeList.add("20:29");
        timeList.add("20:35");
        timeList.add("20:55");

        motionsList.add("正面劈刀");
        motionsList.add("切返");
        motionsList.add("送足");
        motionsList.add("右挽劈刀");
        motionsList.add("跳躍擺陣");
        motionsList.add("右胴劈刀");
        motionsList.add("打小支");

        countList.add(10);
        countList.add(9);
        countList.add(10);
        countList.add(10);
        countList.add(10);
        countList.add(10);
        countList.add(10);

        correctRateList.add(80.0);
        correctRateList.add(77.8);
        correctRateList.add(90.0);
        correctRateList.add(90.0);
        correctRateList.add(70.0);
        correctRateList.add(60.0);
        correctRateList.add(80.0);

        adapter = new RecyclerAdapterForHistoryList(timeList, motionsList, countList, correctRateList, HistoryDailyRecord.this);
        recyclerView.setAdapter(adapter);
    }
}