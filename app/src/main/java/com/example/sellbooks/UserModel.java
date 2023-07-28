package com.example.sellbooks;

public class UserModel {

    private String user_email;
    private String user_name;
    private String user_phone;
    private String user_pw;


    public UserModel() {
    }

    public UserModel(String user_email, String user_name, String user_pw, String user_phone) {
        this.user_email = user_email;
        this.user_phone = user_phone;
        this.user_name = user_name;
        this.user_pw = user_pw;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pw() {
        return user_pw;
    }

    public void setUser_pw(String user_pw) {
        this.user_pw = user_pw;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }
}
