package com.bs.book.service.order;

public enum OrderStatus {

    // 订单状态
    ORDER_STATUS_CREATED(101, "订单已创建，等待卖家发货"),
    ORDER_STATUS_ACCEPTED(102, "卖家已接单，等待卖家发货"),
    ORDER_STATUS_SENT(103, "卖家已发货, 等待买家收货"),

    // 订单结束状态
    ORDER_STATUS_SUCCESS(201, "交易成功"),
    ORDER_STATUS_FAIL(202, "交易失败"),
    ;
    int status;
    String description;
    OrderStatus(int status, String description){
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
