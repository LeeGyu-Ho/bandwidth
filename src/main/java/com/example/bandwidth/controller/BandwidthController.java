package com.example.bandwidth.controller;

import com.example.bandwidth.entity.FileUpload;
import com.example.bandwidth.service.FileUploadBuffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Bandwidth", description = "Bandwidth 예측")
@RequestMapping("/bandwidth")
@RestController
public class BandwidthController {

    @Autowired
    private FileUploadBuffer buffer;

    @PostMapping("/add")
    @Operation(summary = "파일 전송 로그 삽입", description = "파일 전송 로그 삽입")
    public ResponseEntity add(@RequestBody List<FileUpload> fileUpload) {
        log.info("add {}", fileUpload);
        buffer.add(fileUpload);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/printLogQueue")
    @Operation(summary = "Log Queue 출력", description = "처리 대기중인 log 출력")
    public ResponseEntity printLogQueue() {
        buffer.printLogQueue();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/printTimeQueue")
    @Operation(summary = "Time Queue 출력", description = "현재 Buffer에 저장하고 있는 시간 출력")
    public ResponseEntity printTimeQueue() {
        buffer.printTimeQueue();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/predict")
    @Operation(summary = "bandwidth 예측", description = "bandwidth 예측")
    public ResponseEntity predict() throws Exception {
        return new ResponseEntity(buffer.predict(), HttpStatus.OK);
    }

}
