package com.kendohamster;

public class HistoryDataModel {
    String timestamp, action_name, accuracy, practice_time, F_avg, delta_theta;

    public HistoryDataModel(String timestamp, String action_name, String accuracy, String practice_time, String f_avg, String delta_theta) {
        this.timestamp = timestamp;
        this.action_name = action_name;
        this.accuracy = accuracy;
        this.practice_time = practice_time;
        this.F_avg = f_avg;
        this.delta_theta = delta_theta;
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

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getPractice_time() {
        return practice_time;
    }

    public void setPractice_time(String practice_time) {
        this.practice_time = practice_time;
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
}
