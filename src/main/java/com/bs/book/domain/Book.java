package com.bs.book.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "user_book")
@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User set
    Long userId;
    String description;
    String isbn;
    String bookType;
    float sellPrice;
    String currentImg;
    int aim;
    Date createTime;
    Date modifyTime;
    Boolean removed;
    int status;

    // Not need set
    String title;
    String author;
    String oriPrice;
    String publisher;
    String oriImg;
    String summary;
    String catalog;
    String rating;
}
