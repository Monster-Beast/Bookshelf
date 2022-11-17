package com.jnu.bookshelf.adapter;

import android.content.Context;
import android.content.Intent;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jnu.bookshelf.Bean.Bookbean;

import com.jnu.bookshelf.MainActivity;
import com.jnu.bookshelf.R;

import java.util.ArrayList;


public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.MyViewHolder>{
    public ArrayList<Bookbean> data;  //临时的数据
    private Context mContext;
    private int position;
    ActivityResultLauncher<Intent> requestDataLauncher;
    public BookRecyclerViewAdapter(Context mContext,ArrayList<Bookbean> data){
        this.mContext=mContext;
        this.data=data;
    }

    public int getContextMenuPosition() { return position; }
    public void setContextMenuPosition(int position) { this.position = position; }
    @NonNull
    @Override
    public BookRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null) {
            mContext = parent.getContext();
        }
        MyViewHolder holder=new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookRecyclerViewAdapter.MyViewHolder holder, int position) {
        //holder.iv.setImageResource(data.get(position).getBookPic());
        Glide.with(mContext).load(data.get(position).getBookPic()).into(holder.iv);
        holder.tv_bookname.setText(data.get(position).getBookName());
        holder.tv_authername.setText(data.get(position).getAutherName());
        holder.tv_publisher.setText(data.get(position).getPublisher());
        holder.tv_publishedDate.setText(data.get(position).getPublishedDate());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setContextMenuPosition(holder.getLayoutPosition());
                return false;
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContextMenuPosition(holder.getLayoutPosition());
                ((MainActivity)mContext).communication();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView iv;
        TextView tv_bookname;
        TextView tv_authername;
        TextView tv_publisher;
        TextView tv_publishedDate;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.book_iv);
            tv_bookname=itemView.findViewById(R.id.bookname_tv);
            tv_authername=itemView.findViewById(R.id.authername_tv);
            tv_publisher=itemView.findViewById(R.id.publisher_tv);
            tv_publishedDate=itemView.findViewById(R.id.publishedDate_tv);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


            Bookbean book = data.get(getContextMenuPosition());
            menu.setHeaderTitle(book.getBookName());
            ((MainActivity)mContext).CreateMenu(menu);
        }

    }
}
