package com.kendohamster.instructionModel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kendohamster.R;
import com.kendohamster.instructionModel.Instruction;

import java.util.ArrayList;

public class InstructionItemAdapter extends RecyclerView.Adapter<InstructionItemAdapter.ItemViewHolder>{
    private final Context context;
    private final ArrayList<Instruction> dataSet;

    public InstructionItemAdapter(Context context, ArrayList<Instruction> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instruction_list_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Instruction item = dataSet.get(position);

        holder.textView.setText(item.getStringId());
        holder.imageView.setImageResource(item.getImageId());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private TextView textView;
        private ImageView imageView;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = view.findViewById(R.id.item_image);
            textView = view.findViewById(R.id.item_title);
        }
    }
}
