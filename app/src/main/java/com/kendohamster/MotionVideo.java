package com.kendohamster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MotionVideo extends AppCompatActivity {

    Button btnStartPractice, btnAddToMenu;
    String motionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_video);

        btnStartPractice = findViewById(R.id.btnStartPractice);
        btnAddToMenu = findViewById(R.id.btnAddToMenu);
        motionName = "正面劈刀";

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VideoFragment videoFragment = new VideoFragment();
        fragmentTransaction.add(R.id.frame,videoFragment);
        fragmentTransaction.commit();

        btnStartPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
                if(fragment != null) { getSupportFragmentManager().beginTransaction().remove(fragment).commit(); }
*/
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                PracticeTimeFragment practiceTimeFragment = new PracticeTimeFragment();

                Bundle bundle = new Bundle();
                bundle.putString("motionName", motionName);
                practiceTimeFragment.setArguments(bundle);

                fragmentTransaction.addToBackStack("PracticeTimeFragment");
                fragmentTransaction.add(R.id.framePracticeTime, practiceTimeFragment);
                fragmentTransaction.commit();

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
        Intent i = new Intent(this, TrainingView.class);
        i.putExtra("motionName", motionName);
        i.putExtra("practiceTime", practiceTime);
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
}