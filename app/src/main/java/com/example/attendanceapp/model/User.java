package com.example.attendanceapp.model;

import java.util.List;

public class User {

    public String  mUserName , email, password , phone   , id , stdid ;
    public int type ;
    public List<String> subs;




    public List<String> getSubs() {
        return subs;
    }

    public void setSubs(List<String> subs) {
        this.subs = subs;
    }

    public User(List<String> subs) {
        this.subs = subs;
    }

    public User() {

    }

    public User(String mUserName, String email, String password , String  phone , String stdid ,  int type , String id ,List<String> subs  ) {

        this.mUserName = mUserName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.stdid = stdid;
        this.type = type;
        this.id = id;
        this.subs = subs;




    }

    public User(String mUserName, String email, String id) {
        this.mUserName = mUserName;
        this.email = email;
        this.id = id;
    }

    public String getStdid() {
        return stdid;
    }

    public void setStdid(String stdid) {
        this.stdid = stdid;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }



    public String getPassword() {
        return password;
    }



    public String getUserName() {
        return mUserName;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
