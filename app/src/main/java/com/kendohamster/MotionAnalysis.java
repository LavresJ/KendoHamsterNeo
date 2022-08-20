package com.kendohamster;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MotionAnalysis {
    private DatabaseReference DB_ref;

    private ArrayList<Float> acceleration =  new ArrayList<Float>();
    private ArrayList<Float> ang_vel_y =  new ArrayList<Float>();
    private ArrayList<Float> omega =  new ArrayList<Float>();
    private ArrayList<Timestamp> timestamp =  new ArrayList<Timestamp>();

    public ArrayList<Float> F_avg = new ArrayList<Float>();
    public ArrayList<Float> delta_theta = new ArrayList<Float>();

    public MotionAnalysis(){
        DB_ref = FirebaseDatabase.getInstance().getReference();
    }

    public void getData(){
        DatabaseReference objRef = DB_ref.child("RawDataModel");

        objRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot tasksSnapshot) {
                String timestamp_str;
                for (DataSnapshot snapshot: tasksSnapshot.getChildren()) {
                    acceleration.add(((Double) snapshot.child("acceleration").getValue()).floatValue());
                    ang_vel_y.add(((Double) snapshot.child("ang_vel_y").getValue()).floatValue());
                    omega.add(((Double) snapshot.child("omega").getValue()).floatValue());
                    timestamp_str = (String) snapshot.child("timestamp").getValue();
                    timestamp.add(Timestamp.valueOf(timestamp_str));
                }
                //Log.d("del", Long.toString(timestamp.get(1).getTimestamp() - timestamp.get(0).getTimestamp()));
                analysis();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error);
            }
        });
    }

    public void analysis(){
        ArrayList<Float> alpha_y =  new ArrayList<Float>();
        ArrayList<Float> time_sec =  new ArrayList<Float>();

        Long time_0_milli = timestamp.get(0).getTime();

        for(int i = 0; i < timestamp.size(); i++){
            time_sec.add((float) (timestamp.get(i).getTime() - time_0_milli) / 1000);
        }
        //Log.d("omy", ang_vel_y.toString());

        float delta_ang_vel_y;
        float dt;
        for(int i = 0; i < ang_vel_y.size() - 1; i++){
            delta_ang_vel_y = ang_vel_y.get(i + 1) - ang_vel_y.get(i + 1);
            dt = time_sec.get(i + 1) - time_sec.get(i);
            if(dt == 0) {
                alpha_y.add(alpha_y.get(i - 1));
            }
            else {
                alpha_y.add(delta_ang_vel_y / dt);
            }
        }

        ArrayList<Integer> local_min_index = new ArrayList<Integer>();
        ArrayList<Integer> local_max_index = new ArrayList<Integer>();
        int tem_min_i = 0;
        int tem_max_i = 0;
        for(int i = 0; i < alpha_y.size(); i++) {
            if (Math.abs(alpha_y.get(i)) <= 7 && ang_vel_y.get(i) < -4) {
                if (i - tem_min_i >= 20) {
                    local_min_index.add(i);
                }
                tem_min_i = i;
            }
            else if(Math.abs(alpha_y.get(i)) <= 7 && ang_vel_y.get(i) > 3) {
                if (i - tem_max_i >= 20) {
                    local_max_index.add(i);
                }
                tem_max_i = i;
            }
        }
        Log.d("max", local_max_index.toString());
        Log.d("min", local_min_index.toString());

        ArrayList<Integer> swing_start_pt = new ArrayList<Integer>();
        ArrayList<Integer> swing_middle_pt = new ArrayList<Integer>();
        ArrayList<Integer> swing_end_pt = new ArrayList<Integer>();

        for(int i : local_max_index){
            for (int j = i; j > i - 200; j--){
                if (Math.abs(ang_vel_y.get(j)) <= 0.6) {
                    swing_start_pt.add(j);
                    break;
                }
            }
        }
        for(int i : local_min_index){
            for (int j = i; j > i - 200; j--){
                if (Math.abs(ang_vel_y.get(j)) <= 0.6) {
                    swing_middle_pt.add(j);
                    break;
                }
            }
        }
        for(int i : local_min_index){
            for (int j = i; j < i + 200; j++){
                if (Math.abs(ang_vel_y.get(j)) <= 0.6) {
                    swing_end_pt.add(j);
                    break;
                }
            }
        }

        int swing_num = swing_middle_pt.size();
        //ArrayList<Float> F_avg = cal_F_avg(swing_middle_pt, swing_end_pt, swing_num);
        //ArrayList<Float> delta_theta = cal_delta_theta(swing_middle_pt, swing_end_pt, time_sec, swing_num);
        F_avg.clear();
        delta_theta.clear();
        F_avg = cal_F_avg(swing_middle_pt, swing_end_pt, swing_num);
        delta_theta = cal_delta_theta(swing_middle_pt, swing_end_pt, time_sec, swing_num);

        Log.d("F", F_avg.toString());
        Log.d("theta", delta_theta.toString());

        //sendResultToFirebase(timestamp.get(0), "正面劈刀", F_avg, delta_theta);
    }

    public ArrayList<Float> cal_F_avg(ArrayList<Integer> swing_middle_pt, ArrayList<Integer> swing_end_pt, int swing_num) {
        ArrayList<Float> F_avg = new ArrayList<Float>();
        float m = 1.2f;
        for (int i = 0; i < swing_num; i++) {
            float acceleration_total = 0f;
            int sample_num = 0;

            for(int j = swing_middle_pt.get(i); j < swing_end_pt.get(i) + 1; j++){
                acceleration_total += acceleration.get(j);
                sample_num++;
            }
            float acceleration_avg = acceleration_total / sample_num;
            F_avg.add(m * acceleration_avg);
        }
        return F_avg;
    }
    public ArrayList<Float> cal_delta_theta(ArrayList<Integer> swing_middle_pt, ArrayList<Integer> swing_end_pt, ArrayList<Float> time_sec, int swing_num){
        ArrayList<Float> delta_theta = new ArrayList<Float>();
        float pi = 3.1415926f;
        for (int i = 0; i < swing_num; i++) {
            float dt = 0f;
            float delta_theta_total = 0f;

            for(int j = swing_middle_pt.get(i); j < swing_end_pt.get(i) + 1; j++){
                dt = time_sec.get(j + 1) - time_sec.get(j);
                delta_theta_total += Math.abs(ang_vel_y.get(j)) * dt;
            }
            delta_theta.add(delta_theta_total * 180 / pi);
        }
        return delta_theta;
    }
    /*
    public void sendResultToFirebase(Timestamp timestamp, String action,ArrayList<Float> F_avg, ArrayList<Float> delta_theta) {
        //DAOResultDataModel dao = new DAOResultDataModel();
        //ResultDataModel result = new ResultDataModel(timestamp.toString(), action, F_avg.toString(), delta_theta.toString());
        DAOWatchDataModel dao = new DAOWatchDataModel();
        WatchResultModel result = new WatchResultModel(F_avg.toString(), delta_theta.toString());
        dao.add(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Toast.makeText(, "Record is inserted", Toast.LENGTH_SHORT).show();
                Log.d("send", "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("send", "false");
            }
        });
    }

     */
}
