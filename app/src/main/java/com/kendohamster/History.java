package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.navigation.NavigationView;
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

public class History extends AppCompatActivity {
    CalendarView calendarView;
    TextView txtResults;

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
        setContentView(R.layout.activity_history);

        calendarView = findViewById(R.id.calendarView);
        txtResults = findViewById(R.id.txtResults);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(History.this));

        txtResults.setText("");

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.drawer_open , R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {  super .onDrawerClosed(drawerView);  }

            @Override
            public void onDrawerOpened(View drawerView) {  super .onDrawerOpened(drawerView); }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawerLayout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();
                if (id == R.id.action_action){
                    selectItem(R.id.action_action);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (id == R.id.action_menu){
                    selectItem(R.id.action_menu);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();

                    return true;
                }
                else if (id == R.id.action_history){
                    selectItem(R.id.action_history);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (id == R.id.action_setting){
                    selectItem(R.id.action_setting);

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                return false;
            }
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Toast.makeText(this, "選擇 " + year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
            Log.d("year", String.valueOf(year));
            Log.d("month", String.valueOf(month + 1));
            Log.d("dayOfMonth", String.valueOf(dayOfMonth));

            action_name.clear();
            start_time.clear();
            practice_count.clear();
            accuracy_list.clear();
            image_list.clear();
            history_details.clear();

            //Intent i = new Intent(History.this, HistoryDailyRecordCard.class);
            //startActivity(i);
            addDailyRecord(year, month + 1, dayOfMonth);
        });

    }

    public void addDailyRecord(int year, int month, int dayOfMonth){
        DB_ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference objRef = DB_ref.child("HistoryDataModel");
        objRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot taskSnapshot) {
                for(DataSnapshot snapshot: taskSnapshot.getChildren()){
                    addRecords(snapshot, year, month, dayOfMonth);
                }

                adapter = new RecyclerAdapterCard(action_name, start_time, practice_count, accuracy_list, image_list, history_details, History.this);
                recyclerView.setAdapter(adapter);
                txtResults.setText(String.format("%04d-%02d-%02d 有 %d 筆訓練紀錄", year, month, dayOfMonth, action_name.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addRecords(DataSnapshot snapshot, int year, int month, int dayOfMonth){
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
        boolean same_date = false;
        same_date = compare_date(year, month, dayOfMonth, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        if(same_date) {
            action_name.add(motionName);
            start_time.add(String.format("開始時間: %02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
            accuracy_list.add(String.format("正確率: %.2f%%", Double.valueOf(accuracy.toString())));

            switch (motionName) {
                case "正面劈刀":
                    practice_count.add(String.format("練習次數: %d次", practice_time));
                    historyDetails = new HistoryDetailsModel(convert_string_to_float(jsonF), convert_string_to_float(jsonA));
                    history_details.add(historyDetails);
                    image_list.add(R.drawable.hamster1);
                    break;
                case "擦足":
                    practice_count.add(String.format("練習次數: %d次", practice_time));
                    historyDetails = new HistoryDetailsModel(null, null);
                    history_details.add(historyDetails);
                    image_list.add(R.drawable.hamster2);
                    break;
                case "托刀":
                    practice_count.add(String.format("練習時間: %d秒", practice_time));
                    historyDetails = new HistoryDetailsModel(null, null);
                    history_details.add(historyDetails);
                    image_list.add(R.drawable.hamster3);
                    break;
                case "右胴劈刀":
                    practice_count.add(String.format("練習次數: %d次", practice_time));
                    historyDetails = new HistoryDetailsModel(convert_string_to_float(jsonF), convert_string_to_float(jsonA));
                    history_details.add(historyDetails);
                    image_list.add(R.drawable.hamster4);
                    break;
            }
        }
    }

    public boolean compare_date(int select_year, int select_month, int select_day, int record_year, int record_month, int record_day){
        boolean same_date = false;

        if(select_year == record_year && select_month==record_month && select_day==record_day){
            same_date = true;
        }else{
            same_date = false;
        }

        return same_date;
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

    public void selectItem(int position) {
        Intent i = null;
        switch(position) {
            case (R.id.action_action):
                i = new Intent(this,MotionList.class);
                break;
            case R.id.action_history:
                i = new Intent(this, History.class);
                break;
            case R.id.action_menu:
                i = new Intent(this, TrainingMenu.class);
                break;
            case R.id.action_setting:
                i = new Intent(this, Settings.class);
                break;
            default:
                break;
        }

        startActivity(i);
        finish();
    }

}