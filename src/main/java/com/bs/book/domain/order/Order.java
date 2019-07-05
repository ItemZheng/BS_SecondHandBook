package com.bs.book.domain.order;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "book_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    long bookId;
    long fromUserId;
    long toUserId;
    int type;
    String address;
    int status;
    Date createTime;
    Date modifyTime;
    boolean removed;
}
