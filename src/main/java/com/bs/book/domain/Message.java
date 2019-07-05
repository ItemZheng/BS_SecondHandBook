package com.bs.book.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "message")
@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    int type;
    long orderId;
    long fromId;
    long toId;
    String msg;
    boolean readStatus;
    Date createTime;
    Date modifyTime;
    boolean removed;
}
