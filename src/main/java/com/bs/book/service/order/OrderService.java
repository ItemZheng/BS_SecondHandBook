package com.bs.book.service.order;

import com.bs.book.controller.MessageClient;
import com.bs.book.dal.*;
import com.bs.book.domain.Book;
import com.bs.book.domain.Message;
import com.bs.book.domain.User;
import com.bs.book.domain.order.Order;
import com.bs.book.domain.order.Record;
import com.bs.book.dto.OrderDetailDTO;
import com.bs.book.util.Constant;
import com.bs.book.util.ErrorEnum;
import com.bs.book.util.ServiceException;
import com.bs.book.util.Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;

@Service
public class OrderService {
    @Resource
    OrderRepository orderDb;

    @Resource
    RecordRepository recordDb;

    @Resource
    BookRepository bookDb;

    @Resource
    MessageRepository messageDb;

    @Resource
    UserRepository userDb;

    @Resource
    Transaction transaction;

    // fromId user id 是卖家
    public void create(long bookId, long toUserId, int type, String address) throws ServiceException {
        Book book = bookDb.getBookByIdAndRemoved(bookId, false);
        long fromUserId = 0;
        if(book != null){
            fromUserId = book.getUserId();
        }
        // validate
        validate(bookId, fromUserId, toUserId);

        // check type
        if (type != Constant.ORDER_TYPE_OFFLINE && type != Constant.ORDER_TYPE_MAIL) {
            throw new ServiceException(ErrorEnum.ERROR_UNSUPPORT_ORDER_TYPE);
        }

        // create order
        Order order = new Order();
        order.setBookId(bookId);
        order.setFromUserId(fromUserId);
        order.setToUserId(toUserId);
        order.setType(type);
        order.setAddress(Util.limitStringLength(address, 4000));
        order.setStatus(OrderStatus.ORDER_STATUS_CREATED.getStatus());
        Date dateNow = new Date();
        order.setCreateTime(dateNow);
        order.setModifyTime(dateNow);
        order.setRemoved(false);

        // create record
        Record record = new Record();
        record.setOpcode(Constant.ORDER_ACTION_CREATE);
        record.setOperator(toUserId);
        record.setModifyTime(dateNow);
        record.setCreateTime(dateNow);
        record.setRemoved(false);

        // create
        try {
            transaction.createOrder(order, record);
            Message message = buildNotifyMessage(
                    Constant.MESSAGE_TYPE_ORDER_CREATED, order.getId(), toUserId, fromUserId);
            MessageClient.sendMessage(message);
            messageDb.save(message);
        } catch (Exception e) {
            throw new ServiceException(ErrorEnum.ERROR_UNKNOWN);
        }
    }

    public void acceptOrRefuse(long orderId, long fromUserId, boolean isRefuse) throws ServiceException {
        // Get order
        Order order = orderDb.getOrderByIdAndRemoved(orderId, false);
        if (order == null) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_NOT_EXIST);
        }
        // check
        if (order.getStatus() != OrderStatus.ORDER_STATUS_CREATED.getStatus()
                || order.getType() != Constant.ORDER_TYPE_MAIL) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_STATUS_INVALID);
        }
        // check permission
        if (fromUserId != order.getFromUserId()) {
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }

        // Add action
        Date timeNow = new Date();
        if (!isRefuse)
            order.setStatus(OrderStatus.ORDER_STATUS_ACCEPTED.status);
        else
            order.setStatus(OrderStatus.ORDER_STATUS_FAIL.status);
        order.setModifyTime(timeNow);

        Record record = new Record();
        record.setOrderId(orderId);
        if (!isRefuse)
            record.setOpcode(Constant.ORDER_ACTION_ACCEPT);
        else
            record.setOpcode(Constant.ORDER_ACTION_REFUSE);
        record.setOperator(fromUserId);
        record.setCreateTime(timeNow);
        record.setModifyTime(timeNow);
        record.setRemoved(false);

        // save
        if (isRefuse)
            transaction.refuseOrder(order, record);
        else
            transaction.acceptOrder(order, record);

        // send message
        Message message;
        if(isRefuse)
            message = buildNotifyMessage(
                Constant.MESSAGE_TYPE_ORDER_REFUSE, order.getId(), order.getFromUserId(), order.getToUserId());
        else
            message = buildNotifyMessage(
                    Constant.MESSAGE_TYPE_ORDER_ACCEPTED, order.getId(), order.getFromUserId(), order.getToUserId());
        MessageClient.sendMessage(message);
        messageDb.save(message);
    }

    public void send(long orderId, long fromUserId) throws ServiceException {
        // Get order
        Order order = orderDb.getOrderByIdAndRemoved(orderId, false);
        if (order == null) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_NOT_EXIST);
        }
        // check
        if (order.getStatus() != OrderStatus.ORDER_STATUS_ACCEPTED.getStatus()
                || order.getType() != Constant.ORDER_TYPE_MAIL) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_STATUS_INVALID);
        }

        // check permission
        if (fromUserId != order.getFromUserId()) {
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }

        // Add action
        Date timeNow = new Date();
        order.setStatus(OrderStatus.ORDER_STATUS_SENT.status);
        order.setModifyTime(timeNow);

        Record record = new Record();
        record.setOrderId(orderId);
        record.setOpcode(Constant.ORDER_ACTION_SEND);
        record.setOperator(fromUserId);
        record.setCreateTime(timeNow);
        record.setModifyTime(timeNow);
        record.setRemoved(false);

        // save
        transaction.send(order, record);
        // send message
        Message message = buildNotifyMessage(
                    Constant.MESSAGE_TYPE_ORDER_SEND, order.getId(), order.getFromUserId(), order.getToUserId());
        MessageClient.sendMessage(message);
        messageDb.save(message);
    }

    public void receive(long orderId, long toUserId) throws ServiceException {
        // Get order
        Order order = orderDb.getOrderByIdAndRemoved(orderId, false);
        if (order == null) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_NOT_EXIST);
        }
        // check
        if (order.getStatus() != OrderStatus.ORDER_STATUS_SENT.getStatus()
                || order.getType() != Constant.ORDER_TYPE_MAIL) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_STATUS_INVALID);
        }
        // check permission
        if (toUserId != order.getToUserId()) {
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }

        // Add action
        Date timeNow = new Date();
        order.setStatus(OrderStatus.ORDER_STATUS_SUCCESS.status);
        order.setModifyTime(timeNow);

        Record record = new Record();
        record.setOrderId(orderId);
        record.setOpcode(Constant.ORDER_ACTION_RECEIVE);
        record.setOperator(toUserId);
        record.setCreateTime(timeNow);
        record.setModifyTime(timeNow);
        record.setRemoved(false);

        // save
        transaction.receive(order, record);
        // send message
        Message message = buildNotifyMessage(
                Constant.MESSAGE_TYPE_ORDER_RECEIVE, order.getId(), order.getToUserId(), order.getFromUserId());
        MessageClient.sendMessage(message);
        messageDb.save(message);
    }

    public void cancel(long orderId, long toUserId) throws ServiceException {
        // Get order
        Order order = orderDb.getOrderByIdAndRemoved(orderId, false);
        if (order == null) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_NOT_EXIST);
        }
        // check
        if (order.getStatus() != OrderStatus.ORDER_STATUS_CREATED.getStatus()
                || order.getType() != Constant.ORDER_TYPE_MAIL) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_STATUS_INVALID);
        }
        // check permission
        if (toUserId != order.getToUserId()) {
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }

        // Add action
        Date timeNow = new Date();
        order.setStatus(OrderStatus.ORDER_STATUS_FAIL.status);
        order.setModifyTime(timeNow);

        Record record = new Record();
        record.setOrderId(orderId);
        record.setOpcode(Constant.ORDER_ACTION_CANCEL);
        record.setOperator(toUserId);
        record.setCreateTime(timeNow);
        record.setModifyTime(timeNow);
        record.setRemoved(false);

        // Book
        Book book = bookDb.getBookByIdAndRemoved(order.getBookId(), false);
        book.setStatus(Constant.Book_Status_Origin);
        book.setModifyTime(timeNow);
        // save
        transaction.offlineUpdate(order, record, book);

        // send message
        Message message = buildNotifyMessage(
                Constant.MESSAGE_TYPE_ORDER_CANCEL, order.getId(), order.getToUserId(), order.getFromUserId());
        MessageClient.sendMessage(message);
        messageDb.save(message);
    }

    public void offlineUpdate(long orderId, long toUserId, boolean isSuccess) throws ServiceException {
        // Get order
        Order order = orderDb.getOrderByIdAndRemoved(orderId, false);
        if (order == null) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_NOT_EXIST);
        }
        // check
        if (order.getStatus() != OrderStatus.ORDER_STATUS_CREATED.getStatus()
                || order.getType() != Constant.ORDER_TYPE_OFFLINE) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_STATUS_INVALID);
        }
        // check permission
        if (toUserId != order.getToUserId()) {
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }

        // Add action
        Date timeNow = new Date();
        if (isSuccess)
            order.setStatus(OrderStatus.ORDER_STATUS_SUCCESS.status);
        else
            order.setStatus(OrderStatus.ORDER_STATUS_FAIL.status);
        order.setModifyTime(timeNow);

        Record record = new Record();
        record.setOrderId(orderId);
        if (isSuccess)
            record.setOpcode(Constant.ORDER_ACTION_SUCCESS);
        else
            record.setOpcode(Constant.ORDER_ACTION_FAIL);
        record.setOperator(toUserId);
        record.setCreateTime(timeNow);
        record.setModifyTime(timeNow);
        record.setRemoved(false);

        // change book
        Book book = bookDb.getBookByIdAndRemoved(order.getBookId(), false);
        if (isSuccess)
            book.setStatus(Constant.Book_Status_Sold);
        else
            book.setStatus(Constant.Book_Status_Origin);
        book.setModifyTime(timeNow);

        // save
        transaction.offlineUpdate(order, record, book);
        // send message
        Message message;
        if(isSuccess)
            message = buildNotifyMessage(
                    Constant.MESSAGE_TYPE_ORDER_OFFLINE_SUCCESS, order.getId(), order.getToUserId(), order.getFromUserId());
        else
            message = buildNotifyMessage(
                    Constant.MESSAGE_TYPE_ORDER_OFFLINE_FAIL, order.getId(), order.getToUserId(), order.getFromUserId());
        MessageClient.sendMessage(message);
        messageDb.save(message);
    }

    public OrderDetailDTO detail(long orderId, long userId) throws ServiceException {
        // Get Order
        Order order = orderDb.getOrderByIdAndRemoved(orderId, false);
        if (order == null) {
            throw new ServiceException(ErrorEnum.ERROR_ORDER_NOT_EXIST);
        }

        // auth
        if (order.getToUserId() != userId && order.getFromUserId() != userId) {
            throw new ServiceException(ErrorEnum.ERROR_NO_PERMISSION);
        }

        // Get Book
        Book book = bookDb.getOne(orderId);
        if (book == null) {
            throw new ServiceException(ErrorEnum.ERROR_BOOK_NOT_EXIST);
        }

        // Record
        ArrayList<Record> records = recordDb.getAllByOrderIdAndRemoved(orderId, false);

        // get detail
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setBook(book);
        orderDetailDTO.setOrder(order);
        orderDetailDTO.setRecords(records);
        return orderDetailDTO;
    }

    private void validate(long bookId, long fromUserId, long toUserId) throws ServiceException {
        // check book
        Book book = bookDb.getBookByIdAndRemoved(bookId, false);
        if (book == null) {
            throw new ServiceException(ErrorEnum.ERROR_BOOK_NOT_EXIST);
        }
        if (book.getStatus() != Constant.Book_Status_Origin || book.getAim() != Constant.Book_Aim_Sell) {
            throw new ServiceException(ErrorEnum.ERROR_BOOK_STATUS_INVALID);
        }

        // check toId userId
        if (fromUserId == toUserId) {
            throw new ServiceException(ErrorEnum.ERROR_BUY_BOOK_FROM_SELF);
        }

        // check userId
        User user = userDb.findByIdAndRemoved(fromUserId, false);
        if (user == null) {
            throw new ServiceException(ErrorEnum.ERROR_USER_NOT_EXIST);
        }
    }

    private Message buildNotifyMessage(int type, long orderId, long from, long to) {
        Message message = new Message();
        message.setFromId(from);
        message.setToId(to);
        message.setMsg("");
        message.setType(type);
        message.setOrderId(orderId);
        message.setReadStatus(false);
        message.setRemoved(false);
        // time
        Date timeNow = new Date();
        message.setCreateTime(timeNow);
        message.setModifyTime(timeNow);
        return message;
    }
}
