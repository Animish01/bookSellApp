package com.example.sellbooks;

import java.io.Serializable;

public class BookModel implements Serializable {

    private String bookname;
    private String bookdesc;
    private String bookprice;
    private String userid;
    private String imgUrl;
    private String bookId;
    private Boolean sold;


    public BookModel() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public BookModel(String bookname, String bookdesc, String bookprice, String userid, String imgUrl, Boolean sold, String bookId) {
        this.bookname = bookname;
        this.bookdesc = bookdesc;
        this.bookprice = bookprice;
        this.userid = userid;
        this.bookId = bookId;
        this.imgUrl = imgUrl;
        this.sold = sold;
    }

    public String getbookname() {
        return bookname;
    }

    public void setbookname(String bookname) {
        this.bookname = bookname;
    }

    public String getbookdesc() {
        return bookdesc;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setbookdesc(String bookdesc) {
        this.bookdesc = bookdesc;
    }

    public String getbookprice() {
        return bookprice;
    }

    public void setbookprice(String bookprice) {
        this.bookprice = bookprice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }
}
