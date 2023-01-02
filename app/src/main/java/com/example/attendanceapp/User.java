package com.example.attendanceapp;
public class User {

    public String  mUserName , email, password , phone  ;
    public int type ;



    public User() {

    }

    public User(String mUserName, String email, String password , String  phone , int type ) {

        this.mUserName = mUserName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.type = type;




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
