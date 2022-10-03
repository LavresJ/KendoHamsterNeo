package com.kendohamster;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterCard extends RecyclerView.Adapter<RecyclerAdapterCard.CardViewHolder>{
    private ArrayList<String> action_name;
    private ArrayList<String> start_time;
    private ArrayList<String> practice_count;
    private ArrayList<String> accuracy_list;
    private ArrayList<Integer> image_list;

    private ArrayList<HistoryDetailsModel> history_details;

    private Context context;

    public RecyclerAdapterCard(ArrayList<String> action_name, ArrayList<String> start_time, ArrayList<String> practice_count, ArrayList<String> accuracy_list, ArrayList<Integer> image_list, ArrayList<HistoryDetailsModel> history_details, Context context) {
        this.action_name = action_name;
        this.start_time = start_time;
        this.practice_count = practice_count;
        this.accuracy_list = accuracy_list;
        this.image_list = image_list;
        this.history_details = history_details;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        String lan = "en";
        switch (lan){
            case "zh":
                holder.txtViewActionName.setText(action_name.get(position));
                break;
            case "en":
                switch (action_name.get(position)){
                    case ("正面劈刀"):
                        holder.txtViewActionName.setText("Men Uchi");
                        break;
                    case ("擦足"):
                        holder.txtViewActionName.setText("Suri Ashi");
                        break;
                    case ("托刀"):
                        holder.txtViewActionName.setText("Waki Kiamae");
                        break;
                    case ("右胴劈刀"):
                        holder.txtViewActionName.setText("Dou Uchi");
                        break;
                }

                break;
        }


        holder.txtViewStartTime.setText(start_time.get(position));
        holder.txtViewCount.setText(practice_count.get(position));
        holder.txtViewAccuracy.setText(accuracy_list.get(position));
        holder.imageView.setImageResource(image_list.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (action_name.get(position)) {
                    case "正面劈刀":
                        Toast.makeText(context, "正面劈刀", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, DrawHistoryDetails.class);

                        float f_avg[] = new float[history_details.get(position).getF_avg().size()];
                        float delta_theta[] = new float[history_details.get(position).getDelta_theta().size()];

                        //Log.d("f_avg", history_details.get(position).getF_avg().toString());
                        for (int j = 0; j < history_details.get(position).getF_avg().size(); j++) {
                            f_avg[j] = history_details.get(position).getF_avg().get(j);
                            delta_theta[j] = history_details.get(position).getDelta_theta().get(j);
                        }

                        i.putExtra("f_avg", f_avg);
                        i.putExtra("delta_theta", delta_theta);

                        context.startActivity(i);
                        break;
                    case "擦足":
                        Toast.makeText(context, "擦足", Toast.LENGTH_SHORT).show();
                        break;
                    case "托刀":
                        Toast.makeText(context, "托刀", Toast.LENGTH_SHORT).show();
                        break;
                    case "右胴劈刀":
                        Toast.makeText(context, "右胴劈刀", Toast.LENGTH_SHORT).show();
                        Intent i2 = new Intent(context, DrawHistoryDetails.class);

                        float f_avg2[] = new float[history_details.get(position).getF_avg().size()];
                        float delta_theta2[] = new float[history_details.get(position).getDelta_theta().size()];

                        //Log.d("f_avg", history_details.get(position).getF_avg().toString());
                        for (int j = 0; j < history_details.get(position).getF_avg().size(); j++) {
                            f_avg2[j] = history_details.get(position).getF_avg().get(j);
                            delta_theta2[j] = history_details.get(position).getDelta_theta().get(j);
                        }

                        i2.putExtra("f_avg", f_avg2);
                        i2.putExtra("delta_theta", delta_theta2);

                        context.startActivity(i2);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return action_name.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewActionName, txtViewStartTime, txtViewCount, txtViewAccuracy;
        private ImageView imageView;
        private CardView cardView;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            txtViewActionName = itemView.findViewById(R.id.txtViewActionName);
            txtViewStartTime = itemView.findViewById(R.id.txtViewStartTime);
            txtViewCount = itemView.findViewById(R.id.txtViewCount);
            txtViewAccuracy = itemView.findViewById(R.id.txtViewAccuracy);
            imageView = itemView.findViewById(R.id.imageView);

            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
