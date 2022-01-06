package com.example.bandwidth.service;

import com.example.bandwidth.entity.FileUpload;
import com.example.bandwidth.service.BandwidthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;


@Slf4j
@Component
public class FileUploadBuffer {

    private static final int MINUTE_TO_MS = 60000;
    private static final ConcurrentLinkedDeque<FileUpload> logQueue = new ConcurrentLinkedDeque<>();    // Log 저장용 Queue. 모든 값은 여기로 먼저 들어옴
    private static final ConcurrentLinkedDeque<Long> timeQueue = new ConcurrentLinkedDeque<>();         // Time 저장용 Queue. Map에서 값을 보존할 시간을 저장함. 보존기간이 만료되면 Queue에서 사라짐.
    private static final ConcurrentHashMap<Long, Double> map = new ConcurrentHashMap<>();             // 전송용량 저장용 Map. 분당 전송한 용량을 갖고 있음. timeQueue가 만료될 때 값이 삭제됨.

    @Value("${bandwidth.queue.size}")
    private int queueSize;

    @Autowired
    private BandwidthService service;

    private void refresh() {
        long now = System.currentTimeMillis() / MINUTE_TO_MS;           // 분단위 절사를 위한 나눗셈
        log.info("Refresh at {}", new Timestamp(now * MINUTE_TO_MS));
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

    private void parsing() {
        int size = logQueue.size();
        for(int i = 0; i < size ; i++) {
            FileUpload fileUpload = logQueue.poll();
            long startTime = fileUpload.getStartTime().getTime() / MINUTE_TO_MS;
            long endTime = fileUpload.getEndTime().getTime() / MINUTE_TO_MS + 1;
            double averageSpeed = fileUpload.getFileSize() / (endTime - startTime);
            for (long queueTime : timeQueue) {
                if (queueTime >= startTime && queueTime < endTime) {
                    if(map.containsKey(queueTime)) {
                        double oldValue = map.get(queueTime);
                        map.put(queueTime, averageSpeed + oldValue);
                    } else {
                        map.put(queueTime, averageSpeed);
                    }
                }
            }
        }
    }

    public void printLogQueue() {
        for (FileUpload fileUpload : logQueue) {
            log.info("LogQueue : {}", fileUpload);
        }
    }

    public void printTimeQueue() {
        refresh();
        parsing();
        for (Long aLong : timeQueue) {
            log.info("TimeQueue : {} Value : {}", new Timestamp(aLong * MINUTE_TO_MS), map.get(aLong));
        }
    }

    public double predict() throws Exception {
        refresh();
        parsing();
        return service.combine(new ArrayList<>(map.values()));
    }

}
