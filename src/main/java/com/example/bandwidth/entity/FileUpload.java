package com.example.bandwidth.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {

    private double fileSize;
    private Timestamp startTime;
    private Timestamp endTime;

    public long getTerm() {
        return endTime.getTime() - startTime.getTime();
    }

}
