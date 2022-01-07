package com.example.bandwidth.service;

import com.example.bandwidth.entity.FileUpload;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Bandwidth {

    private static final int MINUTE_TO_MS = 60000;
    private static final int MINUTE_TO_SECOND = 60;
    private static final ConcurrentLinkedDeque<FileUpload> logQueue = new ConcurrentLinkedDeque<>();    // Log 저장용 Queue. 모든 값은 여기로 먼저 들어옴
    private static final ConcurrentLinkedDeque<Long> timeQueue = new ConcurrentLinkedDeque<>();         // Time 저장용 Queue. Map에서 값을 보존할 시간을 저장함. 보존기간이 만료되면 Queue에서 사라짐.
    private static final ConcurrentHashMap<Long, Double> map = new ConcurrentHashMap<>();               // 전송용량 저장용 Map. 분당 전송한 용량을 갖고 있음. timeQueue가 만료될 때 값이 삭제됨.
    private static final BandwidthCalculate service = new BandwidthCalculate();

    private int queueSize = 60;

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    // 일정 기간이 지난 Data 삭제
    private void refresh() {
        long now = System.currentTimeMillis() / MINUTE_TO_MS;           // 초단위 절사를 위한 나눗셈
        for(long time : timeQueue) {
            if (time < now - queueSize + 1) {       // 만료시 삭제
                map.remove(time);
            }
        }
        Long last = timeQueue.peekLast();
        if(last == null) {
            last = now - queueSize;
        }
        for(long i = last; i < now; i++) {
            addTimeQueue(i + 1);
        }
    }

    private void addTimeQueue(long time) {
        if(timeQueue.size() >= queueSize) {
            timeQueue.poll();
        }
        timeQueue.add(time);
    }

    public void add(List<FileUpload> fileUpload) {
        logQueue.addAll(fileUpload);
    }

    public void add(FileUpload fileUpload) {
        logQueue.add(fileUpload);
    }

    // logQueue에 쌓인 FileUpload log들을 분단위로 나누어 저장.
    private void parsing() {
        int size = logQueue.size();
        for(int i = 0; i < size ; i++) {
            FileUpload fileUpload = logQueue.poll();
            long startTime = fileUpload.getStartTime().getTime() / MINUTE_TO_MS;
            long endTime = fileUpload.getEndTime().getTime() / MINUTE_TO_MS + 1;
            double averageSpeed = fileUpload.getFileSize() / (endTime - startTime) / MINUTE_TO_SECOND ;
            for (long queueTime : timeQueue) {
                if (queueTime >= startTime && queueTime < endTime) {
                    map.merge(queueTime, averageSpeed, (k, v) -> map.get(queueTime) + averageSpeed);
                }
            }
        }
    }

    public void clear() {
        timeQueue.clear();
        map.clear();
    }

    public void printLogQueue() {
        for (FileUpload fileUpload : logQueue) {
            System.out.println("LogQueue: " + fileUpload);
        }
    }

    public void printTimeQueue() {
        for (Long aLong : timeQueue) {
            System.out.println("TimeQueue: "+ new Timestamp(aLong * MINUTE_TO_MS) +" Value: " + map.get(aLong));
        }
    }

    public synchronized double predict(Timestamp start, Timestamp end) throws Exception {
        if(!logQueue.isEmpty()) {
            refresh();
            parsing();
        }

        long startTime = start.getTime()/MINUTE_TO_MS;
        long endTime = end.getTime()/MINUTE_TO_MS;

        List<Double> valueList = new ArrayList<>();
        for(long l : timeQueue) {
            if(l>=startTime&&l<endTime&&map.containsKey(l)) {
                valueList.add(map.get(l));
            }
        }
        return service.predict(valueList);
    }

    private Bandwidth(){}

    public static Bandwidth getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final Bandwidth INSTANCE = new Bandwidth();
    }

}
