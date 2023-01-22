package com.example.attendanceapp;

public class Attendance {
    String name  ;

    CharSequence date;

    public Attendance(String name, CharSequence date) {
        this.name = name;
        this.date = date;
    }

    public Attendance() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CharSequence getDate() {
        return date;
    }

    public void setDate(CharSequence date) {
        this.date = date;
    }
}
