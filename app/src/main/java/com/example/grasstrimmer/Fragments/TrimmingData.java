package com.example.grasstrimmer.Fragments;

public class TrimmingData {
    String date;
    Long sessionLength;

    public TrimmingData(String date, Long sessionLength) {
        this.date = date;
        this.sessionLength = sessionLength;
    }

    String getDate() {
        return this.date;
    }

    Long getSessionLength() {
        return this.sessionLength;
    }

    void setDate(String date) {
        this.date = date;
    }

    void setSessionLength(Long sessionLength) {
        this.sessionLength = sessionLength;
    }
}
