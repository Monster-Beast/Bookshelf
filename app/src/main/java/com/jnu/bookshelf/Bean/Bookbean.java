package com.jnu.bookshelf.Bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Bookbean implements Serializable {



    private static final String JSON_bookName="bookName";   //书名
    private static final String JSON_autherName="autherName";   //作者名
    private static final String JSON_publisher="publisher";  //出版社
    private static final String JSON_publishedDate="publishedDate"; //出版日期
    private static final String JSON_readingStatus="readingStatus";//阅读状态
    private static final String JSON_label="label";//标签
    private static final String JSON_address="address";//地址
    private static final String JSON_bookPic="bookPic";//图片

    private int id;
    private static final long serialVersionUID= 1L;
    private String bookName;   //书名
    private String autherName;   //作者名
    private String publisher;  //出版社
    private String publishedDate; //出版日期
    private String readingStatus;//阅读状态
    private Labelbean label;//标签
    private String address;//地址
    private String bookPic;//图片
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_bookName, bookName);
        json.put(JSON_autherName, autherName);
        json.put(JSON_publisher, publisher);
        json.put(JSON_publishedDate, publishedDate);
        json.put(JSON_readingStatus, readingStatus);
        json.put(JSON_label, label.getName());
        json.put(JSON_address, address);
        json.put(JSON_bookPic, bookPic);
        return json;
    }
    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAutherName() {
        return autherName;
    }

    public void setAutherName(String autherName) {
        this.autherName = autherName;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public Labelbean getLabel() {
        return label;
    }

    public void setLabel(Labelbean label) {
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBookPic() {
        return bookPic;
    }

    public void setBookPic(String bookPic) {
        this.bookPic = bookPic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
