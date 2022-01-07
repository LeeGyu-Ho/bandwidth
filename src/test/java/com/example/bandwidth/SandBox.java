package com.example.bandwidth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SandBox {

    private static final int MINUTE_TO_MS = 60000;
    private static final int QUEUE_SIZE = 60;

    public static void main(String[] args) throws IOException {
        /*for(int i = 1; i <= 50; i++) {
            int rand = (int) (Math.random() * 1000 * i) - i*500;
            System.out.printf("INSERT INTO fileupload(file_size, start_time, end_time) VALUES (%d, '2022-01-04 00:%02d:00.000', '2022-01-04 00:%02d:10.000')\n", (i*1000 + rand), i, i);
        }*/
//-----------------------------------------------------
//        List<Integer> list = new ArrayList<>();
//
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//
//        List<Integer> subList = list.subList(1, 3);

//        for(int i : subList) {
//            System.out.println(i);
//        }
//-----------------------------------------------------
//        Map<String, Object> map = new HashMap();
//
//        map.remove("테스트");
//-----------------------------------------------------
//        Map<Integer, String> map = new HashMap<>();
//        for(int i = 0; i < 100; i++) {
//            map.put(i, String.valueOf(i));
//        }
//        int big = 40;
//        int small = 20;
//
//        List<String> list = new ArrayList<>();
//
//        map.entrySet().stream()
//                .filter(s -> s.getKey()<big&&s.getKey()>small)
//                .forEach(e -> list.add(e.getValue()));
//
//        System.out.print(list);

//-----------------------------------------------------
        long now = System.currentTimeMillis() / MINUTE_TO_MS;
        StringBuilder buffer = new StringBuilder();
        File file = new File("./json.txt");
        FileOutputStream out = new FileOutputStream(file, false);
        buffer.append("[\n  ");
        for(int i = 1; i <= QUEUE_SIZE; i++) {
            int rand = (int) (Math.random() * MINUTE_TO_MS) - MINUTE_TO_MS/2;
            Date start = new Date((now - QUEUE_SIZE + i)*MINUTE_TO_MS);
            Date end = new Date((now - QUEUE_SIZE + i + 1)*MINUTE_TO_MS - 1);
            buffer.append("{\n" +
                    "    \"fileSize\": " + (i*MINUTE_TO_MS) + ",\n" +
                    "    \"startTime\": \"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(start) + "\",\n" +
                    "    \"endTime\": \"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(end) + "\"\n" +
                    "  }, ");
        }
        buffer.delete(buffer.length()-2, buffer.length());
        buffer.append("\n]");
        out.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
    }
}
