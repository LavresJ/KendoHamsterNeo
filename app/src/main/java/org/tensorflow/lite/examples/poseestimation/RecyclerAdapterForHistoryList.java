package org.tensorflow.lite.examples.poseestimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapterForHistoryList extends RecyclerView.Adapter<RecyclerAdapterForHistoryList.HistoryRecordViewHolder> {

    private ArrayList<String> timeList;
    private ArrayList<String> motionsList;
    private  ArrayList<Integer> countList;
    private  ArrayList<Double> correctRateList;
    private Context context;

    public RecyclerAdapterForHistoryList(ArrayList<String> timeList, ArrayList<String> motionsList, ArrayList<Integer> countList, ArrayList<Double> correctRateList, Context context) {
        this.timeList = timeList;
        this.motionsList = motionsList;
        this.countList = countList;
        this.correctRateList = correctRateList;
        this.context = context;
    }

    @NonNull
    @Override
    //define the card_design we made, 會回傳根據card_design 的view
    public HistoryRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_record, parent, false);
        return new HistoryRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecordViewHolder holder, int position) {
        //specify what should be done when the card_design 被做出來，並且與recyclerView 連起來
        holder.textViewTime.setText(timeList.get(position));
        holder.textViewMotionName.setText(motionsList.get(position));
        holder.textViewCount.setText(countList.get(position).toString());
        holder.textViewCorrectRate.setText(correctRateList.get(position).toString() + "%");
        holder.textViewBar.setText("");
        //holder.cardView.setOnClickListener(this);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //點按鈕的時候，會把是點哪一個項目餵進去HistoryDetails這個Activity
                //Intent i = new Intent(view.getContext(), HistoryDetails.class);
                //i.putExtra("location", holder.getAdapterPosition());
                //context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return motionsList.size();
    }


    //represent card_design
    public class HistoryRecordViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTime, textViewMotionName, textViewCount, textViewCorrectRate, textViewBar;
        private CardView cardView;

        public HistoryRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewMotionName = itemView.findViewById(R.id.textViewMotionRecord);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            textViewCorrectRate = itemView.findViewById(R.id.textViewCorrect);
            textViewBar = itemView.findViewById(R.id.bar);
            cardView = itemView.findViewById(R.id.cardViewForHistoryRecord);

        }
    }

}