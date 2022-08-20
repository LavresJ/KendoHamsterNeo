package com.kendohamster;

import java.sql.Timestamp;

public class HistoryDataModel {
    String timestamp, action_name, F_avg, delta_theta;
    float accuracy;
    int practice_time;

    public HistoryDataModel(String timestamp, String action_name, String f_avg, String delta_theta, float accuracy, int practice_time) {
        this.timestamp = timestamp;
        this.action_name = action_name;
        F_avg = f_avg;
        this.delta_theta = delta_theta;
        this.accuracy = accuracy;
        this.practice_time = practice_time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public String getF_avg() {
        return F_avg;
    }

    public void setF_avg(String f_avg) {
        F_avg = f_avg;
    }

    public String getDelta_theta() {
        return delta_theta;
    }

    public void setDelta_theta(String delta_theta) {
        this.delta_theta = delta_theta;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getPractice_time() {
        return practice_time;
    }

    public void setPractice_time(int practice_time) {
        this.practice_time = practice_time;
    }
}
