package com.kendohamster.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.kendohamster.R;

public class History extends AppCompatActivity {
    CalendarView calendarView;
    Button btnRecord, btnDraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        calendarView = findViewById(R.id.calendarView);
        btnRecord = findViewById(R.id.btnRecord);
        btnDraw = findViewById(R.id.btnDraw);

        /*calendarView.getDate();
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Toast.makeText(this,sdf.format(calendarView.getDate()),Toast.LENGTH_SHORT).show();
            }
        });*/

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(History.this, com.kendohamster.tracker.HistoryDailyRecord.class);
                startActivity(i);
            }
        });

        //此按鈕是用於嘗試java繪圖的功能
        //所以點這邊的按鈕，會進到一個 DrawPictureTry 這個 activity 裡
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(History.this, DrawPictureTry.class);
                //startActivity(i);
            }
        });
    }
}