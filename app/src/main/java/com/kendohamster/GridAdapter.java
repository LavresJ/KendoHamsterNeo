package com.kendohamster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> text = new ArrayList<>();

    public GridAdapter(Context context, ArrayList<String> text) {
        this.context = context;
        this.text = text;
    }

    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder") View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.gridview_motionlist_layout, viewGroup, false);

        TextView textView = v.findViewById(R.id.textViewMotionName);
        //assign data to the component
        textView.setText(text.get(i));

        return v;
    }
}
