package com.example.bandwidth;

public class QueryBuilder {

    public static void main(String[] args) {
        for(int i = 1; i <= 50; i++) {
            int rand = (int) (Math.random() * 1000 * i) - i*500;
            System.out.printf("INSERT INTO fileupload(file_size, start_time, end_time) VALUES (%d, '2022-01-04 00:%02d:00.000', '2022-01-04 00:%02d:10.000')\n", (i*1000 + rand), i, i);
        }
    }
}
