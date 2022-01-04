package com.example.bandwidth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity(name = "fileupload")
public class FileUpload {

    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "file_size")
    private double fileSize;
    @Column(name = "start_time")
    private Timestamp startTime;
    @Column(name = "end_time")
    private Timestamp endTime;

    public long getTerm() {
        return endTime.getTime() - startTime.getTime();
    }

}
