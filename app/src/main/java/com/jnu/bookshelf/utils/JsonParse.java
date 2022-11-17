package com.jnu.bookshelf.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jnu.bookshelf.Bean.Bookbean;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonParse {
    private static JsonParse instance;
    private JsonParse(){
    }
    public static JsonParse getInstance(){
        if(instance==null){
            instance=new JsonParse();
        }
        return instance;
    }
    public ArrayList<Bookbean>getBookdata(String json){
        Gson gson=new Gson();
        Type ArrayType=new TypeToken<ArrayList<Bookbean>>(){
        }.getType();
        ArrayList<Bookbean> data=gson.fromJson(json,ArrayType);
        return  data;

    }
}
