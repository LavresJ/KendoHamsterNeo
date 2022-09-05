package com.kendohamster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawHistoryDetails extends AppCompatActivity {
    LineChartData lineChartFData, lineChartAngleData;
    LineChart lineChartF, lineChartAngle;

    //lineChartFData
    //String jsonF = "[17.940159, 17.219608, 16.62263, 16.858213, 16.588774]";
    String jsonF;
    String jsonFD; //去除json字串的 "[", "]"
    String[] jsonFString; //將json字串分割儲存成字串陣列

    float[] f_avg;
    ArrayList<Float> inputF = new ArrayList<>(); //y軸資料(想辦法將json的陣列字串轉到這個浮點數陣列裡)
    ArrayList<String> xDataF = new ArrayList<>(); //放X軸的資料
    //ArrayList<Entry> yData = new ArrayList<>(); //entry是設置x軸和y軸點
    ArrayList<Entry> entriesF = new ArrayList<>(); //entry是設置x軸和y軸點

    //lineChartAngleData (A:angle)
    //String jsonA = "[136.33502, 146.66122, 143.75522, 147.78035, 153.42871]";
    String jsonA;
    String jsonAD; //去除json字串的 "[", "]"
    String[] jsonAString; //將json字串分割儲存成字串陣列

    float[] delta_theta;
    ArrayList<Float> inputA = new ArrayList<>(); //y軸資料(想辦法將json的陣列字串轉到這個浮點數陣列裡)
    ArrayList<String> xDataA = new ArrayList<>(); //放X軸的資料
    ArrayList<Entry> entriesA = new ArrayList<>(); //entry是設置x軸和y軸點

    private DatabaseReference DB_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_history_details);

        lineChartF = findViewById(R.id.lineChart1);
        lineChartFData = new LineChartData(lineChartF,this);
        lineChartAngle = findViewById(R.id.lineChart2);
        lineChartAngleData = new LineChartData(lineChartAngle,this);

        Intent i = getIntent();
        f_avg = i.getFloatArrayExtra("f_avg");
        delta_theta = i.getFloatArrayExtra("delta_theta");

        draw_plot_F(f_avg);
        draw_plot_A(delta_theta);
    }



    public void draw_plot_F(float[] input){
        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 1;i< input.length+1;i++){
            xData.add("" + i + "");
            //yData.add(new Entry(i-1, i));
            entries.add(new Entry(i-1, input[i - 1]));
        }
        lineChartFData.initX(xData);
        lineChartFData.initY(0F,40F); //這邊需要修改，寫條件式找出y陣列的最大最小值，更新:葉說不用，可以寫死
        //lineChartData.initDataSet(yData);
        lineChartFData.initDataSet(entries);
    }

    public void draw_plot_A(float[] input){
        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 1;i< input.length+1;i++){
            xData.add("" + i + "");
            //yData.add(new Entry(i-1, i));
            entries.add(new Entry(i-1, input[i - 1]));
        }
        lineChartAngleData.initX(xData);
        lineChartAngleData.initY(0F,200F); //這邊需要修改，寫條件式找出y陣列的最大最小值，更新:葉說不用，可以寫死
        //lineChartData.initDataSet(yData);
        lineChartAngleData.initDataSet(entries);
    }
}