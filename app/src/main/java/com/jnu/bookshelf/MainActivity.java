package com.jnu.bookshelf;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jnu.bookshelf.Bean.Bookbean;
import com.jnu.bookshelf.Bean.Labelbean;
import com.jnu.bookshelf.adapter.BookRecyclerViewAdapter;
import com.jnu.bookshelf.adapter.DrawerRecyclerViewAdapter;
import com.jnu.bookshelf.adapter.MySelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestDataLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });
        initData();
        initDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setBookName(data.getStringExtra("BookName"));
            book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setAutherName(data.getStringExtra("autherName"));
            book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setPublisher(data.getStringExtra("publisher"));
            book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setPublishedDate(data.getStringExtra("publishedDate"));
            book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setReadingStatus(data.getStringExtra("readingStatus"));
            if(data.getStringExtra("label").equals("")){
                Labelbean labelbean=new Labelbean();
                labelbean.setName("未分类");
                book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setLabel(labelbean);
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
                labelbean.setName(data.getStringExtra("label"));
                book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setLabel(labelbean);
                int j=0;
                for(;j<labelbeans.size();j++){
                    if(labelbeans.get(j).getName().equals(data.getStringExtra("label"))){
                        break;
                    };
                }
                if(j==labelbeans.size()){
                    labelbeans.add(labelbean);
                }

            }
            //book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setLabel(data.getStringExtra("label"));
            book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setAddress(data.getStringExtra("address"));
            //book.get(bookRecyclerViewAdapter.getContextMenuPosition()).setBookPic(data.getStringExtra("BookName"));
            bookRecyclerViewAdapter.notifyDataSetChanged();
            drawerRecyclerViewAdapter.notifyDataSetChanged();
            initSpinner();

        }

    }

    public void communication(){

        Intent intent =new Intent(MainActivity.this,DetialActivity.class);
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

    private void initData(){
//        Log.i("monster",R.drawable.pingfan+"");
//        Log.i("monster",R.drawable.rick+"");
//        Log.i("monster",R.drawable.sanguo+"");
//        Log.i("monster",(R.drawable.shuihu+""));
//        Log.i("monster",R.drawable.honglou+"");
//        Log.i("monster",R.drawable.xiyou+"");
//        Log.i("monster",R.drawable.santi+"");
//        Log.i("monster",R.drawable.huozhe+"");
//        Log.i("monster",R.drawable.anzhou+"");

        try {
            //打开存放在assets文件夹下面的json格式的文件并且放在文件输入流里面
            InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("Book.json"), "UTF-8");
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

                bookbean.setBookPic(object.getInt("bookPic"));
                book.add(bookbean);
            }
        } catch (IOException | JSONException e) {
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
                    if(editText.getText().equals("")){
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

}