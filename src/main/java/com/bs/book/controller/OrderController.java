package com.bs.book.controller;

import com.bs.book.domain.order.Order;
import com.bs.book.service.order.OrderService;
import com.bs.book.util.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController extends BaseController{

    @Resource
    OrderService orderService;

    @RequestMapping("/create")
    public Object create(long bookId, int type, String address){
        try {
            orderService.create(bookId, getUser().getId(), type, address);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("CREATE ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/accept")
    public Object accept(long orderId){
        try {
            orderService.acceptOrRefuse(orderId, getUser().getId(), false);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("ACCEPT ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/refuse")
    public Object refuse(long orderId){
        try {
            orderService.acceptOrRefuse(orderId, getUser().getId(), true);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("REFUSE ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/send")
    public Object send(long orderId){
        try {
            orderService.send(orderId, getUser().getId());
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("SEND ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/receive")
    public Object receive(long orderId){
        try {
            orderService.receive(orderId, getUser().getId());
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("RECEIVE ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/cancel")
    public Object cancel(long orderId){
        try {
            orderService.cancel(orderId, getUser().getId());
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("CANCEL ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/offlineSuccess")
    public Object offlineSuccess(long orderId){
        try {
            orderService.offlineUpdate(orderId, getUser().getId(), true);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("OFFLINE SUCCESS ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/offlineFail")
    public Object offlineFail(long orderId){
        try {
            orderService.offlineUpdate(orderId, getUser().getId(), false);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            log.error("OFFLINE FAIL ORDER ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }

    @RequestMapping("/info")
    public Object info(long orderId){
        try {
            return buildSuccessResp(orderService.detail(orderId, getUser().getId()));
        } catch (ServiceException e){
            log.error("GET ORDER INFO ERROR: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
    }
}
