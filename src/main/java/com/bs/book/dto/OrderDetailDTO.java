package com.bs.book.dto;

import com.bs.book.domain.Book;
import com.bs.book.domain.order.Order;
import com.bs.book.domain.order.Record;
import lombok.Data;

import java.util.ArrayList;

@Data
public class OrderDetailDTO {
    Book book;
    Order order;
    ArrayList<Record> records;
}
