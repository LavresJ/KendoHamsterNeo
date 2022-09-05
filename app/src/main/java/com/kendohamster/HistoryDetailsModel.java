package com.kendohamster;

import java.util.ArrayList;

public class HistoryDetailsModel {
    public ArrayList<Float> f_avg;
    public ArrayList<Float> delta_theta;

    public HistoryDetailsModel(ArrayList<Float> f_avg, ArrayList<Float> delta_theta) {
        this.f_avg = f_avg;
        this.delta_theta = delta_theta;
    }

    public ArrayList<Float> getF_avg() {
        return f_avg;
    }

    public void setF_avg(ArrayList<Float> f_avg) {
        this.f_avg = f_avg;
    }

    public ArrayList<Float> getDelta_theta() {
        return delta_theta;
    }

    public void setDelta_theta(ArrayList<Float> delta_theta) {
        this.delta_theta = delta_theta;
    }
}
