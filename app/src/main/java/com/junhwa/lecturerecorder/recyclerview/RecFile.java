package com.junhwa.lecturerecorder.recyclerview;

import java.text.DecimalFormat;

public class RecFile {
    private String fileName;
    private int duration;
    private long size;

    public RecFile(String fileName, int duration, long size) {
        this.fileName = fileName;
        this.duration = duration;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDuration() {
        DecimalFormat formatter = new DecimalFormat("00");

        int hour = duration / 3600000;
        int min = (duration / 60000) % 60;
        int sec = (duration / 1000) % 60;
        String str = formatter.format(hour) + ":" + formatter.format(min) + ":" + formatter.format(sec);
        return str;
    }

    public int getIntDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
