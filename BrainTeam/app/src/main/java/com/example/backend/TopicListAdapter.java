package com.example.backend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brainteam.R;

import java.util.List;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.ViewHolder> {

    private List<String> mData;
    private List<String> mExtraInformation;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int layoutToUse;
    private boolean withExtraInfo;

    // data is passed into the constructor
    public TopicListAdapter(Context context, List<String> data, List<String> extraInformation, boolean withDelete, boolean withExtraInfo) {
        this.mInflater = LayoutInflater.from(context);
        this.mExtraInformation = extraInformation;
        this.withExtraInfo = withExtraInfo;
        this.mData = data;
        if (withDelete)
        {
            layoutToUse = R.layout.topic_list_with_delete;
        }
        else
        {
            layoutToUse = R.layout.topic_list_checklist;
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(layoutToUse, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String topicName = mData.get(position);
        String extraInformation = mExtraInformation.get(position);
        holder.topicName.setText(topicName);
        if (withExtraInfo) {
            holder.extraInformation.setText(extraInformation);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView topicName;
        TextView extraInformation;
        ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);
            extraInformation = itemView.findViewById(R.id.extraInformation);
            deleteButton = itemView.findViewById(R.id.deleteTopicIcon);
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