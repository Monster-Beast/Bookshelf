package com.jnu.bookshelf.Bean;

import java.io.Serializable;

public class Bookbean implements Serializable {

    private static final long serialVersionUID= 1L;
    private String bookName;   //书名
    private String autherName;   //作者名
    private String publisher;  //出版社
    private String publishedDate; //出版日期
    private String readingStatus;//阅读状态
    private Labelbean label;//标签
    private String address;//地址
    private int bookPic;//图片

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

    public int getBookPic() {
        return bookPic;
    }

    public void setBookPic(int bookPic) {
        this.bookPic = bookPic;
    }
}
