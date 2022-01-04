package com.example.bandwidth.service;

import com.example.bandwidth.entity.FileUpload;
import com.example.bandwidth.repository.FileUploadRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    @Value("${bandwidth.window.interval}")
    private int windowInterval;

    @Autowired
    private FileUploadRepository repository;

    @PostConstruct
    private void init() {
        windowSize *= 60000;    // 분 -> ms
        windowInterval *= 60000;  // 분 -> ms
    }

    public Double combine(Timestamp start, Timestamp end) throws Exception {
        List<Double> average = new ArrayList<>();
        long term = end.getTime() - start.getTime();
        log.info("시작시간: {} 죵료시간: {} 시간간격: {}", start, end, term);
        if(term < windowSize) {
            throw new Exception("시간이 windowSize보다 작음 " + term + " < " + windowSize);
        }
        for(int i = 0; i <= term/windowInterval; i++) {
            Timestamp windowStart = new Timestamp(start.getTime() + i * windowInterval);
            Timestamp windowEnd = new Timestamp(windowStart.getTime() + windowSize - 1);
            if(windowEnd.getTime() > end.getTime()) {
                break;
            }
            List<FileUpload> list = repository.findAllByStartTimeBetween(windowStart, windowEnd);
            double result = 0;
            if(!list.isEmpty()) {
                result = calculate(list);
                average.add(result);
            }
            log.info("window시작: {} window종료: {} 전송건수: {} 평균값: {}", windowStart, windowEnd, list.size(), result);
        }
        log.info("예측결과: {}", round(predict(average)));
        return round(predict(average));
    }

    public Double calculate(List<FileUpload> list){
        double size = 0;
        double time = 0;
        for(FileUpload f:list) {
            size += f.getFileSize();
            time += f.getTerm();
        }
        if(time == 0) {
            throw new ArithmeticException("list의 크기가 0이거나 전송 시간의 합이 0입니다.");
        }
        return (size/time)*1000;
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
