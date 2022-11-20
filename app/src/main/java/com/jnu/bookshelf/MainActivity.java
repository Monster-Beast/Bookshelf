package com.jnu.bookshelf;

import static com.jnu.bookshelf.DetialActivity.getRealPathFromURI;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jnu.bookshelf.Bean.Bookbean;
import com.jnu.bookshelf.Bean.Labelbean;
import com.jnu.bookshelf.adapter.BookRecyclerViewAdapter;
import com.jnu.bookshelf.adapter.DrawerRecyclerViewAdapter;
import com.jnu.bookshelf.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton add;
    private ActivityResultLauncher Picturerequest;
    private TextView tv_ID;
    private ImageView iv_ID;
    private DrawerLayout drawerLayout;//滑动菜单
    private Button button_drawer;
    private TextView drawer_tvlabel;
    private DrawerRecyclerViewAdapter drawerRecyclerViewAdapter;
    private ArrayList<Bookbean> book=new ArrayList<>();
    private ArrayList<Bookbean> data=new ArrayList<>();  //临时数据
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;
    private RecyclerView recyclerView,drawer_recyclerView;
    ActivityResultLauncher<Intent> requestDataLauncher;
    ArrayAdapter<String> starAdapter;
    SearchView mSearchView;
    private ArrayList<Labelbean> labelbeans=new ArrayList<>();
    private String id;
    private String id_iv;
//   private MHandler mHandler;
private final OkHttpClient client = new OkHttpClient.Builder()
        //添加拦截器

        .build();
private static final int GET = 1;
private static final int REQUEST_Code= 1;
private Handler handler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case GET:
                String string=(String) msg.obj;
                parseJSONWithJSONObject(string);
                break;

        }
        return true;
    }
});
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Picturerequest=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{

        });
        requestDataLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
//        mHandler=new MHandler();
        initData();
        initDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==5 && data.getIntExtra("BookID",-1)!=-1) {
            if(data.getStringExtra("bookPic")!=null) {
                this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setBookPic(data.getStringExtra("bookPic"));
            }
            this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setBookName(data.getStringExtra("BookName"));
            this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setAutherName(data.getStringExtra("autherName"));
            this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setPublisher(data.getStringExtra("publisher"));
            this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setPublishedDate(data.getStringExtra("publishedDate"));
            this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setReadingStatus(data.getStringExtra("readingStatus"));
            if (data.getStringExtra("label").equals("")) {
                Labelbean labelbean = new Labelbean();
                labelbean.setName("未分类");
                this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setLabel(labelbean);
                int j = 0;
                for (; j < labelbeans.size(); j++) {
                    if (labelbeans.get(j).getName().equals("未分类")) {
                        break;
                    }
                    ;
                }
                if (j == labelbeans.size()) {
                    labelbeans.add(labelbean);
                }
            } else {
                Labelbean labelbean = new Labelbean();
                labelbean.setName(data.getStringExtra("label"));
                this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setLabel(labelbean);
                int j = 0;
                for (; j < labelbeans.size(); j++) {
                    if (labelbeans.get(j).getName().equals(data.getStringExtra("label"))) {
                        break;
                    }
                    ;
                }
                if (j == labelbeans.size()) {
                    labelbeans.add(labelbean);
                }

            }
            //book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setLabel(data.getStringExtra("label"));
            this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).setAddress(data.getStringExtra("address"));
            //book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setBookPic(data.getStringExtra("BookName"));
            int i = 0;
            for (; i < book.size(); i++) {
                if (this.data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getId() == book.get(i).getId()) {
                    Bookbean bookbean = new Bookbean();
                    bookbean = this.data.get(bookRecyclerViewAdapter.getContextMenuPosition());
                    book.set(i, bookbean);
                    break;
                }
            }
            if(i==book.size()){
                Bookbean bookbean = new Bookbean();
                bookbean = this.data.get(bookRecyclerViewAdapter.getContextMenuPosition());
                bookbean.setId(book.size());
                book.add(bookbean);
            }
            try {
                Save();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(resultCode==5 && data.getIntExtra("BookID",-1)==-1){
            Bookbean bookbean=new Bookbean();
            if(data.getStringExtra("bookPic")!=null) {
                bookbean.setBookPic(data.getStringExtra("bookPic"));
            }
            bookbean.setBookName(data.getStringExtra("BookName"));
            bookbean.setAutherName(data.getStringExtra("autherName"));
            bookbean.setPublisher(data.getStringExtra("publisher"));
            bookbean.setPublishedDate(data.getStringExtra("publishedDate"));
            bookbean.setReadingStatus(data.getStringExtra("readingStatus"));
            bookbean.setAddress(data.getStringExtra("address"));
            if (data.getStringExtra("label").equals("")) {
                Labelbean labelbean = new Labelbean();
                labelbean.setName("未分类");
                bookbean.setLabel(labelbean);
                int j = 0;
                for (; j < labelbeans.size(); j++) {
                    if (labelbeans.get(j).getName().equals("未分类")) {
                        break;
                    }
                    ;
                }
                if (j == labelbeans.size()) {
                    labelbeans.add(labelbean);
                }
            } else {
                Labelbean labelbean = new Labelbean();
                labelbean.setName(data.getStringExtra("label"));
                bookbean.setLabel(labelbean);
                int j = 0;
                for (; j < labelbeans.size(); j++) {
                    if (labelbeans.get(j).getName().equals(data.getStringExtra("label"))) {
                        break;
                    }
                    ;
                }
                if (j == labelbeans.size()) {
                    labelbeans.add(labelbean);
                }

            }
            bookbean.setId(book.size());
            book.add(bookbean);
            try {
                Save();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(data!=null){
            iv_ID.setImageURI(data.getData());
            SharedPreferences sp=MainActivity.this.getSharedPreferences("Data",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("ID_iv",getRealPathFromURI(this,data.getData()));
            editor.commit();

        }

        bookRecyclerViewAdapter.notifyDataSetChanged();
        drawerRecyclerViewAdapter.notifyDataSetChanged();
        initSpinner();

    }
    public void communication(int requestCode){
        if(requestCode==REQUEST_Code){
            Intent intent =new Intent(MainActivity.this,DetialActivity.class);
            requestDataLauncher.launch(intent);
        }
    }
    public void communication(){

        Intent intent =new Intent(MainActivity.this,DetialActivity.class);
        intent.putExtra("BookID",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getId());
        intent.putExtra("BookName",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getBookName());
        intent.putExtra("autherName",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getAutherName());
        intent.putExtra("publisher",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getPublisher());
        intent.putExtra("publishedDate",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getPublishedDate());
        intent.putExtra("readingStatus",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getReadingStatus());
        intent.putExtra("label",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getLabel().getName());
        //intent.putExtra("label",book.get(bookRecyclerViewAdapter.getContextMenuPosition()).getLabel());
        intent.putExtra("address",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getAddress());
        intent.putExtra("bookPic",data.get(bookRecyclerViewAdapter.getContextMenuPosition()).getBookPic());
        requestDataLauncher.launch(intent);
   }
    private void getDataFormGet() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //原url失效，采用新的代替;
                    String result = get(Constant.WEB_SITE+Constant.Request_Book_URL);

                    Message message = Message.obtain();
                    message.what = GET;
                    message.obj = result;
                    handler.sendMessage(message);
                    ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    private void initData() {
//                getDataFormGet();
//                data.addAll(book);
//        OkHttpClient okHttpCLient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://192.168.137.1:8080/book/Book.json")
//                .build();
//
//        Call call = okHttpCLient.newCall(request);
//        //异步调用,并设置回调函数
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.i("monster","1");
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                String res = response.body().string();
//                Message msg = Message.obtain();
//                msg.what = 2;
//                msg.obj = res;
//                mHandler.sendMessage(msg);
//            }
//        });

//        Log.i("monster",R.drawable.pingfan+"");
//        Log.i("monster",R.drawable.rick+"");
//        Log.i("monster",R.drawable.sanguo+"");
//        Log.i("monster",(R.drawable.shuihu+""));
//        Log.i("monster",R.drawable.honglou+"");
//        Log.i("monster",R.drawable.xiyou+"");
//        Log.i("monster",R.drawable.santi+"");
//        Log.i("monster",R.drawable.huozhe+"");
//        Log.i("monster",R.drawable.anzhou+"");
        SharedPreferences sp=MainActivity.this.getSharedPreferences("Data",Context.MODE_PRIVATE);

        id=sp.getString("ID","ID");
        id_iv=sp.getString("ID_iv",null);
        try {
            //打开存放在assets文件夹下面的json格式的文件并且放在文件输入流里面
            InputStreamReader inputStreamReader=new InputStreamReader(openFileInput("Book.json"));
            //InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("Book.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();

            //新建一个json对象，用它对数据进行操作
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            //单独去一个值
            //Log.i("TESTJSON", "cat=" + jsonObject.getString("cat"));
            JSONArray jsonArray = jsonObject.getJSONArray("book");
            //取一个数组值
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Bookbean bookbean=new Bookbean();
                bookbean.setId(i);
                bookbean.setBookName(object.getString("bookName"));
                bookbean.setAutherName(object.getString("autherName"));
                bookbean.setPublisher(object.getString("publisher"));
                bookbean.setPublishedDate(object.getString("publishedDate"));
                bookbean.setReadingStatus(object.getString("readingStatus"));
                bookbean.setAddress(object.getString("address"));
                if(object.getString("label").equals("")){
                    Labelbean labelbean=new Labelbean();
                    labelbean.setName("未分类");
                    bookbean.setLabel(labelbean);
                    int j=0;
                    for(;j<labelbeans.size();j++){
                        if(labelbeans.get(j).getName().equals("未分类")){
                            break;
                        };
                    }
                    if(j==labelbeans.size()){
                        labelbeans.add(labelbean);
                    }
                }else {
                    Labelbean labelbean=new Labelbean();
                    labelbean.setName(object.getString("label"));
                    bookbean.setLabel(labelbean);
                    int j=0;
                    for(;j<labelbeans.size();j++){
                        if(labelbeans.get(j).getName().equals(object.getString("label"))){
                            break;
                        };
                    }
                    if(j==labelbeans.size()){
                        labelbeans.add(labelbean);
                    }
                }

                bookbean.setBookPic(object.getString("bookPic"));
                book.add(bookbean);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        data.addAll(book);

    }
//    private void downlodefile(Response response, String url, String fileName) {
//        InputStream inputStream = null;
//        byte[] buf = new byte[2048];
//        int len = 0;
//        FileOutputStream outputStream = null;
//        try {
//            inputStream = response.body().byteStream();
//            //文件大小
//            long total = response.body().contentLength();
//            File file = new File(url, fileName);
//            outputStream = new FileOutputStream(file);
//            long sum = 0;
//            while ((len = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, len);
//            }
//            outputStream.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (inputStream != null)
//                    inputStream.close();
//                if (outputStream != null)
//                    outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    class MHandler extends Handler{
//        @Override
//        public void dispatchMessage(@NonNull Message msg) {
//            super.dispatchMessage(msg);
//            switch (msg.what){
//                case 2:
//                    if(msg.obj!=null){
//                        String vlResult=(String) msg.obj;
//                        book= JsonParse.getInstance().getBookdata(vlResult);
//                        data.addAll(book);
//                    }
//            }
//        }
//    }
private void parseJSONWithJSONObject(String JsonData) {
    try {
        JSONArray jsonArray = new JSONObject(JsonData).getJSONArray("Book");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Bookbean bookbean=new Bookbean();
                bookbean.setBookName(object.getString("bookName"));
                bookbean.setAutherName(object.getString("autherName"));
                bookbean.setPublisher(object.getString("publisher"));
                bookbean.setPublishedDate(object.getString("publishedDate"));
                bookbean.setReadingStatus(object.getString("readingStatus"));
                bookbean.setAddress(object.getString("address"));
                if(object.getString("label").equals("")){
                    Labelbean labelbean=new Labelbean();
                    labelbean.setName("未分类");
                    bookbean.setLabel(labelbean);
                    int j=0;
                    for(;j<labelbeans.size();j++){
                        if(labelbeans.get(j).getName().equals("未分类")){
                            break;
                        };
                    }
                    if(j==labelbeans.size()){
                        labelbeans.add(labelbean);
                    }
                }else {
                    Labelbean labelbean=new Labelbean();
                    labelbean.setName(object.getString("label"));
                    bookbean.setLabel(labelbean);
                    int j=0;
                    for(;j<labelbeans.size();j++){
                        if(labelbeans.get(j).getName().equals(object.getString("label"))){
                            break;
                        };
                    }
                    if(j==labelbeans.size()){
                        labelbeans.add(labelbean);
                    }
                }

                bookbean.setBookPic(object.getString("bookPic"));
                book.add(bookbean);

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    data.addAll(book);
}
    private void initRecycler(){
        recyclerView=findViewById(R.id.book_rl);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookRecyclerViewAdapter=new BookRecyclerViewAdapter(MainActivity.this,data);
        recyclerView.setAdapter(bookRecyclerViewAdapter);
        drawer_recyclerView=findViewById(R.id.drawer_rl);
        drawer_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerRecyclerViewAdapter=new DrawerRecyclerViewAdapter(MainActivity.this,labelbeans);
        drawer_recyclerView.setAdapter(drawerRecyclerViewAdapter);

    }

    private void initDrawer(){
        drawerLayout=findViewById(R.id.drawer_layout);
        initDrawer_in();
        initTittleBar();
        initSearch();
        initRecycler();
        add=findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communication(REQUEST_Code);
            }
        });
    }
    private void initSearch(){
        mSearchView=findViewById(R.id.searview);
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "打开搜索框", Toast.LENGTH_SHORT).show();
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(MainActivity.this, "Submit---提交", Toast.LENGTH_SHORT).show();
                int i=0;
                for(;i<book.size();i++){
                    if(book.get(i).getBookName().equals(query)){
                        Toast.makeText(MainActivity.this, "已经找到对应书籍", Toast.LENGTH_SHORT).show();
                        data.clear();
                        data.add(book.get(i));
                        communication();
                        break;
                    }
                }
                if(i==book.size()){
                    Toast.makeText(MainActivity.this, "库中没有此书", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MainActivity.this, "关闭搜索框", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    private void initDrawer_in(){

        iv_ID=findViewById(R.id.ID_iv);
        if(id_iv!=null) {
            Bitmap bitmap = BitmapFactory.decodeFile(id_iv);
            iv_ID.setImageBitmap(bitmap);

        }
        iv_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                Picturerequest.launch(intent);
            }
        });
        tv_ID=findViewById(R.id.tv_id);
        tv_ID.setText(id);
        tv_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请输入要设置的ID").setView(editText).setPositiveButton("确定",new  DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(editText.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this,"空文本无效",Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences sp=MainActivity.this.getSharedPreferences("Data",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("ID",editText.getText().toString());
                            tv_ID.setText(editText.getText());
                            editor.commit();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        drawer_tvlabel=findViewById(R.id.drawer_addlabel);
        drawer_tvlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);  //构造器创AlertDialog构造器
                builder.setIcon(R.drawable.label).setTitle("请输入要设置的标签名").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    private boolean match(String string){
                        int j=0;
                        for(;j<labelbeans.size();j++){
                            if(labelbeans.get(j).getName().equals(string)){
                                break;
                            }
                        }
                        if(j==labelbeans.size()){
                            return false;
                        }else {
                            return true;
                        }
                    }
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    if(editText.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this,"空文本无效",Toast.LENGTH_SHORT).show();
                    }else if(match(editText.getText().toString())){
                        Toast.makeText(MainActivity.this,"标签已存在",Toast.LENGTH_SHORT).show();
                    }else{
                        Labelbean labelbean=new Labelbean();
                        labelbean.setName(editText.getText().toString());
                        labelbeans.add(labelbean);
                        starAdapter.notifyDataSetChanged();
                        drawerRecyclerViewAdapter.notifyDataSetChanged();
                    }

                        bookRecyclerViewAdapter.notifyDataSetChanged();
                        drawerRecyclerViewAdapter.notifyDataSetChanged();
                        initSpinner();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }
    private void initTittleBar(){
        button_drawer=findViewById(R.id.bt_go_drawer);

        button_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        initSpinner();


    }

    private void initSpinner(){
        //声明一个下拉列表的数组适配器
//        Labelbean labelbean=new Labelbean();
//        labelbean.setName("未分类");
//        labelbeans.add(labelbean);
//        labelbean=new Labelbean();
//        labelbean.setName("law");
//        labelbeans.add(labelbean);

        ArrayList<String> starArray = new ArrayList<>();
        for(int i=0;i<labelbeans.size();i++) {
            String string=labelbeans.get(i).getName();
            starArray.add(string);
        }
        starAdapter = new ArrayAdapter<String>(this, R.layout.item_select, starArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_drapdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        Spinner sp = findViewById(R.id.spinner);

        //设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        sp.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.clear();
                for(int i=0;i<book.size();i++){
                    if(book.get(i).getLabel().getName().equals(starArray.get(position))){
                        data.add(book.get(i));
                    }

                }
                bookRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        sp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for(int i=0;i<book.size();i++){
//                    if(book.get(i).getLabel().getName().equals(starArray.get(position))){
//                        data.add(book.get(i));
//                    }
//
//                }
//            }
//        });

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==2){
            delBook(bookRecyclerViewAdapter.getContextMenuPosition());
        }

        return super.onContextItemSelected(item);
    }

    public void CreateMenu(ContextMenu contextMenu) {
        int groupID = 0;
        int order = 0;
        int[] itemID = {2};

        for(int i=0;i<itemID.length;i++)
        {
            switch(itemID[i])
            {
                case 2:
                    contextMenu.add(groupID, itemID[i], order, "删除");
                    break;
                default:
                    break;
            }
        }

    }
    private void delBook(int position){
        int id=data.get(position).getId();
        for(int i=0;i<book.size();i++){
            if(id==book.get(i).getId()){
                book.remove(i);
                for(int j=i;j<book.size();j++){
                    book.get(j).setId(j-1);
                }
                break;
            }
        }
        try {
            Save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bookRecyclerViewAdapter.notifyDataSetChanged();
        drawerRecyclerViewAdapter.notifyDataSetChanged();
        initSpinner();
    }
    private void Save()throws JSONException{
        JSONArray array = new JSONArray();

        for (Bookbean c : book)
            array.put(c.toJSON());
        Writer writer = null;
        try {
            OutputStream out = this.openFileOutput("Book.json",
                    Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write("{\"book\""+":"+array.toString()+"}");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}