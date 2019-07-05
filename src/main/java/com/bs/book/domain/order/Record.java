package com.bs.book.domain.order;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "order_record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    long orderId;
    int opcode;
    long operator;
    Date createTime;
    Date modifyTime;
    boolean removed;
}
