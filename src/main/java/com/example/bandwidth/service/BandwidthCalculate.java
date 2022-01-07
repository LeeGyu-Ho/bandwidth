package com.example.bandwidth.service;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Precision;

import java.util.*;

class BandwidthCalculate {

    private int windowSize = 5;
    private int windowLag = 1;
    private int roundPoint = 3;

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getWindowLag() {
        return windowLag;
    }

    public void setWindowLag(int windowLag) {
        this.windowLag = windowLag;
    }

    public int getRoundPoint() {
        return roundPoint;
    }

    public void setRoundPoint(int roundPoint) {
        this.roundPoint = roundPoint;
    }

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
        return round(bps, roundPoint);
    }

    private double round(double bps, int point) {
        bps = Precision.round(bps, point);
        return bps;
    }
}
