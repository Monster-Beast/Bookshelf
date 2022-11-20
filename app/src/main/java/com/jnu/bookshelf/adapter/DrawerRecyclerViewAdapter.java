package com.jnu.bookshelf.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jnu.bookshelf.Bean.Bookbean;
import com.jnu.bookshelf.Bean.Labelbean;
import com.jnu.bookshelf.MainActivity;
import com.jnu.bookshelf.R;

import java.util.ArrayList;
import java.util.Random;

public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.MyViewHolder>{
    public ArrayList<Labelbean> data;
    private Context mContext;
    private int position;
    public DrawerRecyclerViewAdapter(Context mContext,ArrayList<Labelbean> data){
        this.mContext=mContext;
        this.data=data;
    }
    public int getContextMenuPosition() { return position; }
    public void setContextMenuPosition(int position) { this.position = position; }

    @NonNull
    @Override
    public DrawerRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null) {
            mContext = parent.getContext();
        }
        MyViewHolder holder=new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.drawerrl_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.tv.setText(data.get(position).getName());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setContextMenuPosition(holder.getLayoutPosition());
                return false;
            }

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv=itemView.findViewById(R.id.label_iv);
            tv=itemView.findViewById(R.id.label_tv);

        }


    }


}
