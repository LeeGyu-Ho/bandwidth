package com.example.bandwidth.controller;

import com.example.bandwidth.entity.FileUpload;
import com.example.bandwidth.service.BandwidthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Tag(name = "Bandwidth", description = "Bandwidth 예측")
@RequestMapping("/bandwidth")
@RestController
public class BandwidthController {

    @Autowired
    private BandwidthService service;

    @PostMapping("/average")
    @Operation(summary = "bandwidth 이동평균", description = "bandwidth 이동 평균 계산")
    public ResponseEntity average(@RequestBody List<FileUpload> list) {
        return new ResponseEntity(service.calculate(list), HttpStatus.OK);
    }

    @PostMapping("/predict")
    @Operation(summary = "bandwidth 선형회귀", description = "bandwidth 이동 평균 선형 회귀")
    public ResponseEntity predict(@RequestBody List<Double> list) {
        return new ResponseEntity(service.predict(list), HttpStatus.OK);
    }

    @GetMapping("/combine")
    public ResponseEntity combine(@RequestParam Timestamp start, @RequestParam Timestamp end) throws Exception {
        return new ResponseEntity(service.combine(start, end), HttpStatus.OK);
    }

}
