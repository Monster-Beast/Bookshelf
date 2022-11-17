package com.jnu.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        init();
    }
    void init(){

        ImageView iv=findViewById(R.id.Dbook_iv);
        EditText bookName_et=findViewById(R.id.bookname_et);
        EditText authername_et=findViewById(R.id.authername_et);
        EditText publisher_et=findViewById(R.id.publisher_et);
        EditText publishedDate_et=findViewById(R.id.publishedDate_et);
        EditText state_et=findViewById(R.id.state_et);
        EditText label_et=findViewById(R.id.label_et);
        EditText address_et=findViewById(R.id.address_et);
        Button bt_esc=findViewById(R.id.bt_esc);
        bt_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        Button bt_save=findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookName = bookName_et.getText().toString();
                String authername = authername_et.getText().toString();
                String publisher = publisher_et.getText().toString();
                String publishedDate = publishedDate_et.getText().toString();
                String readingStatus = state_et.getText().toString();
                String label = label_et.getText().toString();
                String address = address_et.getText().toString();
                Intent intent1 =new Intent();
                intent1.putExtra("BookName",bookName);
                intent1.putExtra("autherName",authername);
                intent1.putExtra("publisher",publisher);
                intent1.putExtra("publishedDate",publishedDate);
                intent1.putExtra("readingStatus",readingStatus);
                intent1.putExtra("label",label);
                intent1.putExtra("address",address);
                setResult(RESULT_OK, intent1);
                finish();

            }
        });
        Intent intent=getIntent();
        bookName_et.setText(intent.getStringExtra("BookName"));
        authername_et.setText(intent.getStringExtra("autherName"));
        publisher_et.setText(intent.getStringExtra("publisher"));
        publishedDate_et.setText(intent.getStringExtra("publishedDate"));
        state_et.setText(intent.getStringExtra("readingStatus"));
        label_et.setText(intent.getStringExtra("label"));
        address_et.setText(intent.getStringExtra("address"));
        Glide.with(this).load(intent.getStringExtra("bookPic")).into(iv);
        //iv.setImageResource(intent.getStringExtra("bookPic"));

    }
}