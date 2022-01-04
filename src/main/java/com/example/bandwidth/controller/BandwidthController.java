package com.example.bandwidth.controller;

import com.example.bandwidth.entity.FileUploadEntity;
import com.example.bandwidth.service.BandwidthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Bandwidth", description = "Bandwidth 예측")
@RequestMapping("/bandwidth")
@RestController
public class BandwidthController {

    @Autowired
    private BandwidthService service;

    @PostMapping("/average")
    @Operation(summary = "bandwidth 이동평균", description = "bandwidth 이동 평균 계산")
    public String average(@RequestBody List<FileUploadEntity> list) {
        return service.calculate(list);
    }

    @PostMapping("/predict")
    @Operation(summary = "bandwidth 선형회귀", description = "bandwidth 이동 평균 선형 회귀")
    public String predict(@RequestBody List<Double> list) {
        return service.predict(list);
    }

}
