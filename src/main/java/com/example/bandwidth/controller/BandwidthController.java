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
    public void add(@RequestBody List<FileUpload> fileUpload) {
        log.info("add {}", fileUpload);
        buffer.add(fileUpload);
    }

    @GetMapping("/printLogQueue")
    @Operation(summary = "Log Queue 출력(테스트용)", description = "처리 대기중인 log 출력")
    public void printLogQueue() {
        buffer.printLogQueue();
    }

    @GetMapping("/printTimeQueue")
    @Operation(summary = "Time Queue 출력(테스트용)", description = "현재 Buffer에 저장하고 있는 시간 출력")
    public void printTimeQueue() {
        buffer.printTimeQueue();
    }

    @GetMapping("/predict")
    @Operation(summary = "bandwidth 예측", description = "bandwidth 예측")
    public ResponseEntity<Double> predict() throws Exception {
        return new ResponseEntity<>(buffer.getSpeed(), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Buffer Clear(테스트용)", description = "저장데이터 삭제")
    public void delete() {
        buffer.clear();
    }

}
