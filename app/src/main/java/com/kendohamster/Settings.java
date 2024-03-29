package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kendohamster.R;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    ListView listView1;
    String PersonalInformation[];
    ArrayAdapter<String> adapter1;

    ListView listView2;
    String settingsGeneral[];
    ArrayAdapter<String> adapter2;

    String lan;
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
                selectItem(4);
            }
        });
        //一般設定欄位
        listView2 = findViewById(R.id.listGeneral);
        settingsGeneral = getResources().getStringArray(R.array.settingsGeneral);

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settingsGeneral);
        listView2.setAdapter(adapter2);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                    builder.setTitle(R.string.plsChooseLanguage);

                    String[] languages = {"中文", "English"};

                    builder.setSingleChoiceItems(languages, 3, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    lan = "zh";
                                    break;
                                case 1:
                                    lan = "en";
                                    break;
                            }
                        }
                    });
                    builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            setLocale(lan);
                        }

                    });
                    builder.create().show();
                }
                else {
                    String settingsGeneral = adapterView.getItemAtPosition(i).toString();
                    Toast.makeText(getApplicationContext(), "選擇" + settingsGeneral, Toast.LENGTH_LONG).show();
                }
            }
        });

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
            case 4:
                i = new Intent(this, EmailLogin.class);
            default:
                break;
        }

        startActivity(i);
        finish();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainPage.class);
        finish();
        startActivity(refresh);
    }
}