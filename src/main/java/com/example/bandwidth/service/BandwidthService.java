package com.example.bandwidth.service;

import com.example.bandwidth.entity.FileUploadEntity;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BandwidthService {

    private final String[] unit = {"bps", "Kbps", "Mbps", "Gbps"};
    private final int threshold = 1024;
    private final int point = 3;

    public String calculate(List<FileUploadEntity> list) {
        double size = 0;
        double time = 0;
        for(FileUploadEntity f:list) {
            size += f.getFileSize();
            time += f.getTerm();
        }
        double speed = (size/time)*1000;
        return prettier(speed);
    }

    public String predict(List<Double> averageSpeed) {
        SimpleRegression regression = new SimpleRegression();
        int i;
        for(i = 0; i < averageSpeed.size(); i++) {
            regression.addData(i, averageSpeed.get(i));
        }

        return prettier(regression.predict(i));
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
