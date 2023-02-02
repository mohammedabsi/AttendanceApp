package com.example.attendanceapp.model;

public class Post {

    String postname , postdesc , mImageurl , mCourseid;

    public Post(String postname, String postdesc, String mImageurl , String mCourseid) {
        this.postname = postname;
        this.postdesc = postdesc;
        this.mImageurl = mImageurl;
        this.mCourseid = mCourseid;
    }

    public String getCourseid() {
        return mCourseid;
    }

    public void setCourseid(String courseid) {
        this.mCourseid = courseid;
    }

    public Post() {
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getPostdesc() {
        return postdesc;
    }

    public void setPostdesc(String postdesc) {
        this.postdesc = postdesc;
    }

    public String getmImageurl() {
        return mImageurl;
    }

    public void setmImageurl(String mImageurl) {
        this.mImageurl = mImageurl;
    }
}
