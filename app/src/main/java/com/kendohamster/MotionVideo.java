package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kendohamster.instructionModel.Instruction;
import com.kendohamster.instructionModel.adapter.InstructionItemAdapter;
import com.kendohamster.instructionModel.data.Datasource;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MotionVideo extends AppCompatActivity {

    Button btnStartPractice;
    String motionName;
    long motionId;
    int practiceTime;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_video);

        Intent i = getIntent();
        motionId = i.getIntExtra("position", 0);

        btnStartPractice = findViewById(R.id.btnStartPractice);
        motionName = MotionList.text.get(Math.toIntExact(motionId));

        ///
        ArrayList<Instruction> insList = new ArrayList<>();

        Datasource datasource = new Datasource();

        switch (motionName){
            case "正面劈刀":
            case "Men Uchi":
                insList = datasource.loadMenUchiIns();
                break;
            case "擦足":
            case "Suri Ashi":
                insList = datasource.loadSuriAshiIns();
                break;
            case "托刀":
            case "Waki Kiamae":
                insList = datasource.loadWakiKiamaeIns();
                break;
            case "右胴劈刀":
            case "Dou Uchi":
                insList = datasource.loadDouUchiIns();
        }



        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new InstructionItemAdapter(this, insList));
        ///

        /*
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        VideoFragment videoFragment = new VideoFragment();
        fragmentTransaction.add(R.id.frame,videoFragment);
        fragmentTransaction.commit();

         */

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(motionName);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.drawer_open , R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super .onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super .onDrawerOpened(drawerView);
            }
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


        btnStartPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(motionName){
                    //動態動作
                    case "正面劈刀":
                    case "擦足":
                    case "右胴劈刀":
                    case "Men Uchi":
                    case "Suri Ashi":
                    case "Dou Uchi":
                        AlertDialog.Builder builder = new AlertDialog.Builder(MotionVideo.this);
                        builder.setTitle("請輸入練習次數");

                        final EditText edtPracticeTime = new EditText(MotionVideo.this); //final一個editText
                        edtPracticeTime.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(edtPracticeTime);


                        builder.setPositiveButton("開始練習", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button

                                if(edtPracticeTime.getText().toString().matches("")){
                                    Toast.makeText(MotionVideo.this, "請輸入正整數！", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    practiceTime = Integer.valueOf(edtPracticeTime.getText().toString());
                                    if (practiceTime > 0) { //輸入為正整數
                                        startPracticing(motionName, practiceTime);
                                    } else {
                                        edtPracticeTime.setText("");
                                        Toast.makeText(MotionVideo.this, "請輸入正整數！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        builder.create().show();
                        break;
                    //靜態動作
                    case "托刀":
                    case "Waki Kiamae":
                        startPracticing(motionName, 10);
                        break;
                }
/*
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
                if(fragment != null) { getSupportFragmentManager().beginTransaction().remove(fragment).commit(); }
*/


            }
        });

    }

    public void startPracticing(String motionName, int practiceTime){
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        String timestamp_str = timestamp.toString();
        Log.d("timestamp_str", timestamp_str);

        Intent i = new Intent(this, TrainingView.class);
        i.putExtra("motionName", motionName);
        i.putExtra("practiceTime", practiceTime);
        i.putExtra("camera_back", false);
        i.putExtra("time_start", timestamp_str);
        startActivity(i);
        MotionVideo.this.finish();
    }

    public void selectItem(int position) {
        Intent i = null;
        switch (position) {
            case (R.id.action_action):
                finish();
                break;
            case R.id.action_history:
                i = new Intent(this, History.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_menu:
                i = new Intent(this, TrainingMenu.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_setting:
                i = new Intent(this, Settings.class);
                startActivity(i);
                finish();
                break;
        }
    }
}