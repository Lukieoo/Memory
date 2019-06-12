package com.anioncode.memory.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anioncode.memory.Models.Places;
import com.anioncode.memory.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Recycle_adapter extends RecyclerView.Adapter<Recycle_adapter.ExampleViewHolder> {


    public ArrayList<Places> pong=new ArrayList();
    private OnItemClickListener mListener;

    public Recycle_adapter(ArrayList<Places> pong, OnItemClickListener mListener) {
        this.pong = pong;
        this.mListener = mListener;
    }

//    public void setOnItemClickListener(OnItemClickListener listener) {
//        mListener = listener;
//    }



    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item, parent, false);
        return new ExampleViewHolder(v,mListener);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Places currentItem = pong.get(position);


        String creatorName = currentItem.getName();
        String likeCount = currentItem.getUsername();

        holder.text1.setText(creatorName);
        holder.text2.setText(likeCount);


    }
    @Override
    public int getItemCount() {
        return pong.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView text1;
        public TextView text2;
        private OnItemClickListener mListener;

        public ExampleViewHolder(final View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);

            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            mListener=onItemClickListener;

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());

        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
