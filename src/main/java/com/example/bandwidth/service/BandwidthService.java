package com.example.bandwidth.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class BandwidthService {

    private final String[] unit = {"Bps", "KBps", "MBps", "GBps"};

    @Value("${bandwidth.threshold}")
    private int threshold;
    @Value("${bandwidth.point}")
    private int point;
    @Value("${bandwidth.window.size}")
    private int windowSize;
    @Value("${bandwidth.window.lag}")
    private int windowLag;

    public Double combine(List<Double> speed) throws Exception {
        List<Double> average = new ArrayList<>();
        if(speed.size() < windowSize) {
            throw new Exception("시간이 windowSize보다 작음 " + speed.size() + " < " + windowSize);
        }
        int windowCount = speed.size() / windowLag - windowSize; // 범위 내에 들어갈 window의 개수
        for(int i = 0; i <= windowCount; i++) {
            List<Double> window = speed.subList(i, i + windowSize - 1);
            double result = calculate(window);
            average.add(result);
        }
        log.info("예측결과: {}", round(predict(average)));
        return round(predict(average));
    }

    public Double calculate(List<Double> list){
        double speed = 0;
        for(Double f:list) {
            speed += f;
        }
        if(list.isEmpty()) {
            throw new ArithmeticException("windowSize가 0입니다.");
        }
        return speed / list.size();
    }

    public Double predict(List<Double> averageSpeed) {
        SimpleRegression regression = new SimpleRegression();
        int i;
        if(averageSpeed.size() == 1) {
            return averageSpeed.get(0);
        }
        for(i = 0; i < averageSpeed.size(); i++) {
            regression.addData(i, averageSpeed.get(i));
        }

        return regression.predict(i);
    }

    public String prettier(double bps) {
        int i;
        for(i = 0; bps>threshold; i++) {
            if(i>= unit.length-1) {
                break;
            }
            bps/=1024;
        }
        return round(bps) + unit[i];
    }

    public double round(double bps) {
        return round(bps, point);
    }

    public double round(double bps, int point) {
        bps = Precision.round(bps, point);
        return bps;
    }
}
