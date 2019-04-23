package com.example.backend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.brainteam.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterLauncher extends RecyclerView.Adapter<ListAdapterLauncher.ViewHolder> {

    private List<String> mData;
    private List<Boolean> checked = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public ListAdapterLauncher(Context context, List<String> data)
    {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        for (int i = 0; i < mData.size(); i++)
        {
            checked.add(i, false);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.launcher_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String topicName = mData.get(position);
        holder.topicName.setText(topicName);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(checked.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked.set(position, isChecked);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView topicName;
        CheckBox checkBox;
        ViewHolder(View itemView)
        {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}