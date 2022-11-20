package com.jnu.bookshelf;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jnu.bookshelf.utils.Constant;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetialActivity extends AppCompatActivity {
    private ActivityResultLauncher Picturerequest;
    private int id;
    ImageView iv;
    Uri uri;
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        Picturerequest=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{

        });
        init();
    }
    void init(){
        verifyStoragePermissions(this);
        iv=findViewById(R.id.Dbook_iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                Picturerequest.launch(intent);

            }
        });
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
                DetialActivity.this.finish();
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
                intent1.putExtra("BookID",id);
                if(uri!=null) {
                    intent1.putExtra("bookPic", getRealPathFromURI(DetialActivity.this, uri));
                }
                OkHttpClient client = new OkHttpClient();
                if(uri!=null) {
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            File file = new File(getRealPathFromURI(DetialActivity.this,uri));
                            MediaType mediaType = MediaType.parse("image/jpeg");
                            // 把文件封装进请求体
                            //RequestBody fileBody = RequestBody.create(file,mediaType);
                            RequestBody image = RequestBody.create(file,MediaType.parse("image/*"));
                            //                    MultipartBody multipartBody = new MultipartBody.Builder()
//                            .setType(MultipartBody.FORM)
//                            .addFormDataPart("pic", "1.png", requestBody)
//                            .build();
//                            RequestBody body = new MultipartBody.Builder()
//                                    .setType(MultipartBody.FORM) // 表单类型(必填)
//                                    .addFormDataPart("smfile", file.getName(), fileBody)
//                                    .build();
//                            Request request = new Request.Builder()
//                                    .url(Constant.WEB_SITE+"/img")
//                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0")
//                                    .post(body)
//                                    .build();
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.ALTERNATIVE)

                                    .addFormDataPart("image", getRealPathFromURI(DetialActivity.this,uri), image)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(Constant.WEB_SITE+"/img")
                                    .post(requestBody)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();
                                response.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                            Request request = new Request.Builder()
//                                    .url(Constant.WEB_SITE+"/img")
//                                    .post(requestBody)
//                                    .build();
//                            Call call = client.newCall(request);
//                            try {
//                                Response response = call.execute();
//                                response.close();
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }


                    }.start();



//                    Call call = client.newCall(request);
//                    call.enqueue(new Callback() {
//                        @Override
//                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//                        }
//
//                        @Override
//                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                            String result = response.body().string();
//                        }
//                    });
                }
                setResult(5, intent1);
                finish();

            }
        });
        Intent intent=getIntent();
        id=intent.getIntExtra("BookID",-1);
        bookName_et.setText(intent.getStringExtra("BookName"));
        authername_et.setText(intent.getStringExtra("autherName"));
        publisher_et.setText(intent.getStringExtra("publisher"));
        publishedDate_et.setText(intent.getStringExtra("publishedDate"));
        state_et.setText(intent.getStringExtra("readingStatus"));
        label_et.setText(intent.getStringExtra("label"));
        address_et.setText(intent.getStringExtra("address"));
        if(intent.getStringExtra("bookPic")!=null){
            Glide.with(this).load(intent.getStringExtra("bookPic")).into(iv);
        }
        //iv.setImageResource(intent.getStringExtra("bookPic"));

    }
//    private String post(String url) throws IOException {
//        File file = new File(uri.getPath());
//        RequestBody requestBody = RequestBody.create(file,MediaType.parse("application/octet-stream"));
//        MultipartBody multipartBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("pic", "1.png", requestBody)
//                .build();
//        Request request = new Request.Builder()
//                .url(Constant.WEB_SITE+"/img")
//                .post(multipartBody)
//                .build();
//        Call call = client.newCall(request);
//        Response response = call.execute();
//        return response.body().string();
//    }
//    private void DataFromPost() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    String result = post("https://www.lagou.com/jobs/positionAjax.json", "");
//                    Log.e(TAG, result);
//                    Message msg = Message.obtain();
//                    msg.what = POST;
//                    msg.obj = result;
//                    handler.sendMessage(msg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            // 得到图片的全路径
            uri = data.getData();
            Log.i("monster",uri.getPath()+"");
            iv.setImageURI(uri);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams();
//        params.setMargins(0, 15, 0, 15);
//        if (data != null){
//            Uri mImageCaptureUri = data.getData();
//            if (mImageCaptureUri != null){
//                try {
//                    //这个方法是根据Uri获取Bitmap图片的静态方法
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
//                    // 获取屏幕分辨率
//                    DisplayMetrics dm_2 = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(dm_2);
//                    // 图片分辨率与屏幕分辨率比例
//                    float scale_2 = bitmap.getWidth() / (float) dm_2.widthPixels;
//
//                    Bitmap newBitMap = null;
//                    if (scale_2 > 1) {
//                        newBitMap = zoomBitmap(bitmap, bitmap.getWidth()
//                                / scale_2, bitmap.getHeight() / scale_2);
//                        bitmap.recycle();
//                    }
//
//                    if (newBitMap != null) {
//                        iv.setImageBitmap(newBitMap);
//                    } else {
//                        iv.setImageBitmap(bitmap);
//                    }
//                    iv.setLayoutParams(params);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        }
    }
    /**
     * 将URI路径转化为path路径
     */
    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
