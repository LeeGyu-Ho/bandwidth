package com.example.bandwidth.entity;

import java.sql.Timestamp;

public class FileUpload {

    private double fileSize;
    private Timestamp startTime;
    private Timestamp endTime;

    public FileUpload(double fileSize, Timestamp startTime, Timestamp endTime) {
        this.fileSize = fileSize;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public FileUpload() {
    }


    public long getTerm() {
        return endTime.getTime() - startTime.getTime();
    }

    public double getFileSize() {
        return this.fileSize;
    }

    public Timestamp getStartTime() {
        return this.startTime;
    }

    public Timestamp getEndTime() {
        return this.endTime;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String toString() {
        return "FileUpload(fileSize=" + this.getFileSize() + ", startTime=" + this.getStartTime() + ", endTime=" + this.getEndTime() + ")";
    }
}
