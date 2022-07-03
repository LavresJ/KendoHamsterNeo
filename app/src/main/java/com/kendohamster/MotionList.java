package com.kendohamster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MotionList extends AppCompatActivity {

    GridView gridView;
    ArrayList<String> text = new ArrayList<>();
//    ArrayList<Integer> image = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_list);

        gridView = findViewById(R.id.gridView);
        fillArray();

        GridAdapter adapter = new GridAdapter(this, text);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MotionList.this,MotionVideo.class);
                startActivity(intent);
            }
        });

    }

    public void fillArray(){
        text.add("動作一");
        text.add("動作二");
        text.add("動作三");
        text.add("動作四");
        text.add("動作五");
        text.add("動作六");
        text.add("動作七");
        text.add("動作八");
        text.add("動作九");

    }


}