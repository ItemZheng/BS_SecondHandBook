package com.bs.book.domain.douban;

import lombok.Data;

import java.util.ArrayList;

@Data
public class DoubanBook {
    String title;
    ArrayList<String> author;
    Rating rating;
    String price;
    String publisher;
    String image;
    String summary;
    String catalog;

    @Data
    public class Rating{
        String average;
    }
}
