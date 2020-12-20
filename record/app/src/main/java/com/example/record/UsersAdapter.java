package com.example.record;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> {

    private ArrayList<PersonalData> mList = null;
    private Activity context = null;


    public UsersAdapter(Activity context, ArrayList<PersonalData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView date;
        protected TextView time;
        protected TextView reps;
        protected TextView name;



        public CustomViewHolder(View view) {
            super(view);
            this.date = (TextView) view.findViewById(R.id.textView_list_date);
            this.time = (TextView) view.findViewById(R.id.textView_list_time);
            this.reps = (TextView) view.findViewById(R.id.textView_list_reps);
            this.name = (TextView) view.findViewById(R.id.textView_list_name);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.date.setText(mList.get(position).getDate());
        viewholder.time.setText(mList.get(position).getTime());
        viewholder.reps.setText(mList.get(position).getReps());
        viewholder.name.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}