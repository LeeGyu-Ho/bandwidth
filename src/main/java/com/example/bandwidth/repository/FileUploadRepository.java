package com.example.bandwidth.repository;

import com.example.bandwidth.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface FileUploadRepository  extends JpaRepository<FileUpload, String> {
    List<FileUpload> findAllByStartTimeBetween(Timestamp start, Timestamp end);
}
