package com.bs.book.dal;

import com.bs.book.domain.Book;
import com.bs.book.domain.User;
import com.bs.book.domain.order.Order;
import com.bs.book.domain.order.Record;
import com.bs.book.util.Constant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class Transaction {
    @Resource
    RecordRepository recordDb;

    @Resource
    BookRepository bookDb;

    @Resource
    UserRepository userDb;

    @Resource
    OrderRepository orderDb;

    @Transactional
    public void createOrder(Order order, Record record){
        // save
        orderDb.save(order);

        // record
        record.setOrderId(order.getId());
        recordDb.save(record);

        // change book
        Book book = bookDb.getBookByIdAndRemoved(order.getBookId(), false);
        book.setStatus(Constant.Book_Status_Handling);
        book.setModifyTime(new Date());
        bookDb.save(book);
    }

    @Transactional
    public void acceptOrder(Order order, Record record){
        // save
        orderDb.save(order);
        recordDb.save(record);
    }

    @Transactional
    public void refuseOrder(Order order, Record record){
        // save
        orderDb.save(order);
        recordDb.save(record);
        // change book
        Book book = bookDb.getBookByIdAndRemoved(order.getBookId(), false);
        book.setStatus(Constant.Book_Status_Origin);
        book.setModifyTime(new Date());
        bookDb.save(book);
    }

    @Transactional
    public void receive(Order order, Record record){
        // save
        orderDb.save(order);
        recordDb.save(record);
        // change book
        Book book = bookDb.getBookByIdAndRemoved(order.getBookId(), false);
        book.setStatus(Constant.Book_Status_Sold);
        book.setModifyTime(new Date());
        bookDb.save(book);
    }

    @Transactional
    public void send(Order order, Record record){
        // save
        orderDb.save(order);
        recordDb.save(record);
    }

    @Transactional
    public void offlineUpdate(Order order, Record record, Book book){
        // save
        orderDb.save(order);
        recordDb.save(record);
        bookDb.save(book);
    }
}
