package com.kendohamster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kendohamster.R;

public class TrainingMenu extends AppCompatActivity {
    ListView listView;
    String menu[];
    ArrayAdapter<String> adapter;

    Button buttonAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_menu);


        listView = findViewById(R.id.listGeneral);
        buttonAdd = findViewById(R.id.buttonAddMenu);
        menu = getResources().getStringArray(R.array.menu);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,menu);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String menu = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"選擇" + menu,Toast.LENGTH_LONG).show();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}