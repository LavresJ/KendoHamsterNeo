package com.kendohamster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class MotionVideo2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_video2);

        Intent intent = getIntent();
        int i = intent.getIntExtra("i",0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        VideoFragment2 videoFragment2 = new VideoFragment2();

        Bundle bundle = new Bundle();
        bundle.putInt("i",i);
        videoFragment2.setArguments(bundle);

        fragmentTransaction.add(R.id.frameLayout,videoFragment2);
        fragmentTransaction.commit();
    }
}