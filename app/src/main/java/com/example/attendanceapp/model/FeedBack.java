package com.example.attendanceapp.model;

public class FeedBack {

    String feedId , feedname , feedemail , feedstatus , feedimage ;

    public FeedBack(String feedId ,String feedname, String feedemail,  String feedimage , String feedstatus) {
        this.feedId = feedId;
        this.feedname = feedname;
        this.feedemail = feedemail;
        this.feedimage = feedimage;
        this.feedstatus = feedstatus;
    }

    public FeedBack() {
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedname() {
        return feedname;
    }

    public void setFeedname(String feedname) {
        this.feedname = feedname;
    }

    public String getFeedemail() {
        return feedemail;
    }

    public void setFeedemail(String feedemail) {
        this.feedemail = feedemail;
    }

    public String getFeedstatus() {
        return feedstatus;
    }

    public void setFeedstatus(String feedstatus) {
        this.feedstatus = feedstatus;
    }

    public String getFeedimage() {
        return feedimage;
    }

    public void setFeedimage(String feedimage) {
        this.feedimage = feedimage;
    }
}
