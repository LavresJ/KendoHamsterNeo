package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.app.AlertDialog;
import android.app.Dialog;
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

import java.sql.Timestamp;
import java.util.ArrayList;

public class MotionVideo extends AppCompatActivity {

    Button btnStartPractice, btnAddToMenu;
    String motionName;
    long motionId;
    int practiceTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_video);

        Intent i = getIntent();
        motionId = i.getIntExtra("position", 0);

        btnStartPractice = findViewById(R.id.btnStartPractice);
        btnAddToMenu = findViewById(R.id.btnAddToMenu);
        motionName = MotionList.text.get(Math.toIntExact(motionId));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VideoFragment videoFragment = new VideoFragment();
        fragmentTransaction.add(R.id.frame,videoFragment);
        fragmentTransaction.commit();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                    case"擦足":
                        AlertDialog.Builder builder = new AlertDialog.Builder(MotionVideo.this);
                        builder.setTitle("請輸入練習次數");

                        final EditText edtPracticeTime = new EditText(MotionVideo.this); //final一個editText
                        edtPracticeTime.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setView(edtPracticeTime);

                        builder.setPositiveButton("開始練習", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                practiceTime = Integer.valueOf(edtPracticeTime.getText().toString());
                                if(practiceTime > 0){ //輸入為正整數
                                    Intent i = new Intent(MotionVideo.this, TrainingView.class);
                                    i.putExtra("motionName", motionName);
                                    i.putExtra("practiceTime", practiceTime);
                                    i.putExtra("camera_back", true);
                                    startActivity(i);
                                    MotionVideo.this.finish();
                                }
                                else{
                                    edtPracticeTime.setText("");
                                    Toast.makeText(MotionVideo.this, "請輸入正整數！",Toast.LENGTH_SHORT).show();
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
                        startPracticing(motionName, 10);
                        break;
                }
/*
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
                if(fragment != null) { getSupportFragmentManager().beginTransaction().remove(fragment).commit(); }
*/


            }
        });

        btnAddToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Add to the menu boiiiii",Toast.LENGTH_SHORT).show();
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
        i.putExtra("camera_back", true);
        i.putExtra("time_start", timestamp_str);
        startActivity(i);
        MotionVideo.this.finish();
    }

    //MotionVideo關閉fragment時用到
    public void closeFragment(int frameId){

        Fragment fragment = getSupportFragmentManager().findFragmentById(frameId);
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
    public void selectItem(int position) {
        Intent i = null;
        switch (position) {
            case (R.id.action_action):
                i = new Intent(this, MotionList.class);
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
    }
}