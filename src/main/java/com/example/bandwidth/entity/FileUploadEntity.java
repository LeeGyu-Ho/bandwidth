package com.example.bandwidth.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class FileUploadEntity {

    private double fileSize;

    private Timestamp startTime;

    private Timestamp endTime;

    public long getTerm() {
        return endTime.getTime() - startTime.getTime();
    }

}
