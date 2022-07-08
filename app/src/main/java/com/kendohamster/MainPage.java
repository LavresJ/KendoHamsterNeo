package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainPage extends AppCompatActivity implements View.OnClickListener{

    Button btnMotionList,btnHistory,btnTrainingMenu,btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        btnMotionList = findViewById(R.id.btnMotionList);
        btnHistory = findViewById(R.id.btnHistory);
        btnTrainingMenu =findViewById(R.id.btnTrainingMenu);
        btnSettings = findViewById(R.id.btnSettings);

        btnMotionList.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnTrainingMenu.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawerLayout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();
                if (id == R.id.action_action){
                    Toast.makeText(MainPage.this, item.getTitle() + " pressed", Toast.LENGTH_LONG).show();

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (id == R.id.action_menu){
                    Toast.makeText(MainPage.this, item.getTitle() + " pressed", Toast.LENGTH_LONG).show();

                    item.setChecked(true);
                    drawerLayout.closeDrawers();

                    return true;
                }
                else if (id == R.id.action_history){
                    Toast.makeText(MainPage.this, item.getTitle() + " pressed", Toast.LENGTH_LONG).show();

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (id == R.id.action_setting){
                    Toast.makeText(MainPage.this, item.getTitle() + " pressed", Toast.LENGTH_LONG).show();

                    item.setChecked(true);
                    drawerLayout.closeDrawers();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.btnMotionList:
                i = new Intent(this,MotionList.class);
                break;
            case R.id.btnHistory:
                i = new Intent(this, History.class);
                break;
            case R.id.btnTrainingMenu:
                i = new Intent(this, TrainingMenu.class);
                break;
            case R.id.btnSettings:
                i = new Intent(this, Settings.class);
                break;
            default:
                break;
        }
        startActivity(i);
    }
}