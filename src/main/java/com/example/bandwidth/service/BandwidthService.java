package com.example.bandwidth.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class BandwidthService {

    @Value("${bandwidth.window.size}")
    private int windowSize;
    @Value("${bandwidth.window.lag}")
    private int windowLag;

    public Double predict(List<Double> speed) throws Exception {
        List<Double> average = new ArrayList<>();
        if(speed.size() < windowSize) {
            throw new Exception("시간이 windowSize보다 작음 " + speed.size() + " < " + windowSize);
        }
        int windowCount = speed.size() / windowLag - windowSize; // 범위 내에 들어갈 window의 개수
        for(int i = 0; i <= windowCount; i++) {
            List<Double> window = speed.subList(i, i + windowSize);
            double result = calculate(window);
            average.add(result);
        }
        log.info("예측결과: {}", round(regression(average)));
        return round(regression(average));
    }

    private Double calculate(List<Double> list){
        double speed = 0;
        for(Double f:list) {
            speed += f;
        }
        if(list.isEmpty()) {
            throw new ArithmeticException("windowSize가 0입니다.");
        }
        log.info("Average: {} ListSize: {}", speed / list.size(), list.size());
        return speed / list.size();
    }

    private Double regression(List<Double> averageSpeed) {
        SimpleRegression reg = new SimpleRegression();
        int i;
        if(averageSpeed.size() == 1) {
            return averageSpeed.get(0);
        }
        for(i = 0; i < averageSpeed.size(); i++) {
            reg.addData(i, averageSpeed.get(i));
        }

        return reg.predict(i);
    }

    private double round(double bps) {
        return round(bps, 3);
    }

    private double round(double bps, int point) {
        bps = Precision.round(bps, point);
        return bps;
    }
}
