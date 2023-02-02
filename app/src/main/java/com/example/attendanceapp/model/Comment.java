package com.example.attendanceapp.model;

public class Comment {
    String commentusername , commentText , commentUserId ;

    public Comment(String commentusername, String commentText , String commentUserId) {
        this.commentusername = commentusername;
        this.commentText = commentText;
        this.commentUserId = commentUserId;

    }

    public Comment() {
    }

    public String getCommentusername() {
        return commentusername;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentUserId() {
        return commentUserId;
    }
}
