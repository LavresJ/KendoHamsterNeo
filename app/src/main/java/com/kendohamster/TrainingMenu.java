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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TrainingMenu extends AppCompatActivity {
    ListView listView;
    String menu[];
    ArrayAdapter<String> adapter;

    String menuName[];
    String menuMotion[];

    ArrayList<String> menu_motion_arraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_menu);

        listView = findViewById(R.id.listGeneral);
        menu = getResources().getStringArray(R.array.menu);

        ///////

        //取得菜單名稱list和菜單內的動作list
        menuName = new String[menu.length];
        menuMotion = new String[menu.length];
        for (int i = 0; i < menu.length; i++){
            String[] parts = menu[i].split(";");
            menuName[i] = parts[0];
            menuMotion[i] = parts[1];
        }

        //////

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuName);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String menu_name = adapterView.getItemAtPosition(i).toString();

                String[] motions = menuMotion[i].split(",");

                String dialogMessage = "";
                for (int k = 0; k < motions.length; k++){
                    String[] parts = motions[k].split(":");
                    dialogMessage += "\t";
                    dialogMessage += motions[k];
                    switch (parts[0]){
                        case "正面劈刀":
                        case "擦足":
                        case "右胴劈刀":
                            dialogMessage += "次";
                            break;
                        case "托刀":
                            dialogMessage += "秒";
                            break;
                    }
                    dialogMessage += "\n";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(TrainingMenu.this);
                builder.setMessage(dialogMessage)
                        .setTitle(menu_name);

                builder.setPositiveButton("開始練習", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Toast.makeText(getApplicationContext(),"開始練習" + menu_name,Toast.LENGTH_SHORT).show();
                        menu_motion_arraylist = new ArrayList<String>();
                        for (int k = 0; k < motions.length; k++){
                            menu_motion_arraylist.add(motions[k]);
                        }
                        String[] parts = menu_motion_arraylist.get(0).split(":");
                        Intent i = new Intent(TrainingMenu.this, TrainingView.class);
                        i.putExtra("motionName", parts[0]);
                        i.putExtra("practiceTime", Integer.valueOf(parts[1]));
                        i.putExtra("camera_back", true);
                        i.putExtra("menu_motion_arraylist", menu_motion_arraylist);
                        i.putExtra("from_menu", true);
                        startActivity(i);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.create().show();
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
            default:
                break;
        }

        startActivity(i);
        finish();
    }
}