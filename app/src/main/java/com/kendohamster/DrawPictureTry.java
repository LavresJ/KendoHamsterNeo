package com.kendohamster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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

public class DrawPictureTry extends AppCompatActivity {
    LineChartData lineChartFData, lineChartAngleData;
    LineChart lineChartF, lineChartAngle;

    //lineChartFData
    //String jsonF = "[17.940159, 17.219608, 16.62263, 16.858213, 16.588774]";
    String jsonF;
    String jsonFD; //去除json字串的 "[", "]"
    String[] jsonFString; //將json字串分割儲存成字串陣列

    ArrayList<Float> inputF = new ArrayList<>(); //y軸資料(想辦法將json的陣列字串轉到這個浮點數陣列裡)
    ArrayList<String> xDataF = new ArrayList<>(); //放X軸的資料
    //ArrayList<Entry> yData = new ArrayList<>(); //entry是設置x軸和y軸點
    ArrayList<Entry> entriesF = new ArrayList<>(); //entry是設置x軸和y軸點

    //lineChartAngleData (A:angle)
    //String jsonA = "[136.33502, 146.66122, 143.75522, 147.78035, 153.42871]";
    String jsonA;
    String jsonAD; //去除json字串的 "[", "]"
    String[] jsonAString; //將json字串分割儲存成字串陣列

    ArrayList<Float> inputA = new ArrayList<>(); //y軸資料(想辦法將json的陣列字串轉到這個浮點數陣列裡)
    ArrayList<String> xDataA = new ArrayList<>(); //放X軸的資料
    ArrayList<Entry> entriesA = new ArrayList<>(); //entry是設置x軸和y軸點

    private DatabaseReference DB_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_picture_try);

        lineChartF = findViewById(R.id.lineChart1);
        lineChartFData = new LineChartData(lineChartF,this);
        lineChartAngle = findViewById(R.id.lineChart2);
        lineChartAngleData = new LineChartData(lineChartAngle,this);

        //讀firebase results
        DB_ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference objRef = DB_ref.child("ResultDataModel");
        objRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot taskSnapshot) {
                for(DataSnapshot snapshot: taskSnapshot.getChildren()){
                    jsonF = snapshot.child("f_avg").getValue().toString();
                    jsonA = snapshot.child("delta_theta").getValue().toString();
                }
                Log.d("f_avg", jsonF);
                inputF = convert_string_to_float(jsonF);
                inputA = convert_string_to_float(jsonA);
                draw_plot_F(inputF);
                draw_plot_A(inputA);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        //將lineChartFData餵進lineChartF
        jsonFD = jsonF.substring(1, jsonF.length()-1);
        jsonFString = jsonFD.split(", ");

        //將string陣列轉成ArrayList
        List<String> jsonFStringList =  Arrays.asList(jsonFString);
        ArrayList<String> jsonFStringArrayList = new ArrayList<String>(jsonFStringList);


        for(int i = 0; i < jsonFStringArrayList.size(); i++){
            inputF.add( Float.parseFloat(jsonFStringArrayList.get(i)) );
        }

        for(int i = 1;i< inputF.size()+1;i++){
            xDataF.add("" + i + "");
            //yData.add(new Entry(i-1, i));
            entriesF.add(new Entry(i-1, inputF.get(i - 1)));
        }
        lineChartFData.initX(xDataF);
        lineChartFData.initY(0F,40F); //這邊需要修改，寫條件式找出y陣列的最大最小值，更新:葉說不用，可以寫死
        //lineChartData.initDataSet(yData);
        lineChartFData.initDataSet(entriesF);

        //將lineChartAngleData餵進lineChartAngle
        jsonAD = jsonA.substring(1, jsonA.length()-1);
        jsonAString = jsonAD.split(", ");

        //將string陣列轉成ArrayList
        List<String> jsonAStringList =  Arrays.asList(jsonAString);
        ArrayList<String> jsonAStringArrayList = new ArrayList<String>(jsonAStringList);


        for(int i = 0; i < jsonAStringArrayList.size(); i++){
            inputA.add( Float.parseFloat(jsonAStringArrayList.get(i)) );
        }

        for(int i = 1;i< inputA.size()+1;i++){
            xDataA.add("" + i + "");
            //yData.add(new Entry(i-1, i));
            entriesA.add(new Entry(i-1, inputA.get(i - 1)));
        }
        lineChartAngleData.initX(xDataA);
        lineChartAngleData.initY(0F,180F); //這邊需要修改，寫條件式找出y陣列的最大最小值，更新:葉說不用，可以寫死
        //lineChartData.initDataSet(yData);
        lineChartAngleData.initDataSet(entriesA);

         */
    }

    public ArrayList<Float> convert_string_to_float(String json){
        ArrayList<Float> input = new ArrayList<Float>();
        String jsonD; //去除json字串的 "[", "]"
        String[] jsonString; //將json字串分割儲存成字串陣列

        jsonD = json.substring(1, json.length()-1);
        jsonString = jsonD.split(", ");

        //將string陣列轉成ArrayList
        List<String> jsonStringList =  Arrays.asList(jsonString);
        ArrayList<String> jsonStringArrayList = new ArrayList<String>(jsonStringList);


        for(int i = 0; i < jsonStringArrayList.size(); i++){
            input.add( Float.parseFloat(jsonStringArrayList.get(i)) );
        }

        return input;
    }

    public void draw_plot_F(ArrayList<Float> input){
        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 1;i< input.size()+1;i++){
            xData.add("" + i + "");
            //yData.add(new Entry(i-1, i));
            entries.add(new Entry(i-1, input.get(i - 1)));
        }
        lineChartFData.initX(xData);
        lineChartFData.initY(0F,40F); //這邊需要修改，寫條件式找出y陣列的最大最小值，更新:葉說不用，可以寫死
        //lineChartData.initDataSet(yData);
        lineChartFData.initDataSet(entries);
    }

    public void draw_plot_A(ArrayList<Float> input){
        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 1;i< input.size()+1;i++){
            xData.add("" + i + "");
            //yData.add(new Entry(i-1, i));
            entries.add(new Entry(i-1, input.get(i - 1)));
        }
        lineChartAngleData.initX(xData);
        lineChartAngleData.initY(0F,200F); //這邊需要修改，寫條件式找出y陣列的最大最小值，更新:葉說不用，可以寫死
        //lineChartData.initDataSet(yData);
        lineChartAngleData.initDataSet(entries);
    }
}