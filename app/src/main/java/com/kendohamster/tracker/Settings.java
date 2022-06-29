package com.kendohamster.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kendohamster.R;

public class Settings extends AppCompatActivity {
    ListView listView1;
    String PersonalInformation[];
    ArrayAdapter<String> adapter1;

    ListView listView2;
    String settingsGeneral[];
    ArrayAdapter<String> adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //個人資訊欄位設定
        listView1 = findViewById(R.id.listInformation);
        PersonalInformation = getResources().getStringArray(R.array.PersonalInformation);

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, PersonalInformation);
        listView1.setAdapter(adapter1);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String personalInformation = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"選擇" + personalInformation,Toast.LENGTH_LONG).show();
            }
        });
        //一般設定欄位
        listView2 = findViewById(R.id.listGeneral);
        settingsGeneral = getResources().getStringArray(R.array.settingsGeneral);

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, PersonalInformation);
        listView2.setAdapter(adapter2);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String settingsGeneral = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"選擇" + settingsGeneral,Toast.LENGTH_LONG).show();
            }
        });
    }
}