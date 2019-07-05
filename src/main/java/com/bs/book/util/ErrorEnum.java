package com.bs.book.util;

public enum ErrorEnum {
    ERROR_UNKNOWN(9999, "Unknown Error", "未知错误"),
    ERROR_PASSWORD_ERROR(1, "Password Error", "密码错误"),
    ERROR_USER_NOT_LOGIN(2, "User not login in", "用户未登陆"),
    ERROR_PASSWORD_FORMAT_INVALID(3, "Invalid password format","密码格式不正确"),
    ERROR_USERNAME_FORMAT_INVALID(4, "Invalid username format","用户名格式不正确"),
    ERROR_USERNAME_EXISTS(5, "Username exists", "用户名已存在"),
    ERROR_EMAIL_FORMAT_INVALID(6, "Email format invalid", "邮箱格式不正确"),
    ERROR_EMAIL_EXISTS(7, "Email exists", "邮箱已存在"),
    ERROR_SEND_EMAIL(8, "Send email error", "邮件发送异常"),
    ERROR_NOT_REGISTER(9, "User not register", "用户未注册"),
    ERROR_VERIFICATION_CODE_NOT_MATCH(10, "Verification code not match", "验证码错误"),
    ERROR_SAVE_USER_FAIL(11, "Save user fail", "存储用户错误"),
    ERROR_USERNAME_NOT_EXIST(12, "Username not exist", "用户名不存在"),
    ERROR_SAME_PASSWORD(13, "New password is same toId old password", "新旧密码相同"),
    ERROR_EMAIL_SEND_TOO_FREQUENTLY(14, "Email is sent too frequently", "邮件发送过于频繁"),
    ERROR_EMAIL_NOT_EXISTS(15, "Email not exists", "邮箱不存在"),
    ERROR_SEND_EMAIL_FIRST(16, "Send email first", "找回密码先发送邮件"),
    ERROR_GET_NO_RESPONSE_FROM_HOST(17, "Api call fail", "Api 调用失败"),
    ERROR_BOOK_NOT_FOUND(18, "Book Not Found", "书籍未找到"),
    ERROR_UNEXPECTED_API_RESPONSE(19, "Unexpected api response", "未知api响应"),
    ERROR_BOOK_PRICE_INVALID(20, "Book price invalid", "书籍价格不合法"),
    ERROR_BOOK_PARAMENT_INVALID(21, "Book parament invalid", "书籍参数不合法"),
    ERROR_NO_PERMISSION(22, "No operation permission", "没有操作权限"),
    ERROR_UPLOAD_FAIL(23, "Upload Image Fail", "图片上传失败"),
    ERROR_BOOK_STATUS_INVALID(24, "Book status invalid", "书籍状态异常"),
    ERROR_QUERY_PARAMENT_INVALID(25, "Query parament invalid", "搜索参数不合法"),
    ERROR_BOOK_NOT_EXIST(26, "Book not exist", "书籍不存在"),
    ERROR_BUY_BOOK_FROM_SELF(27, "Buy Book From Self", "从自己这里购买书籍"),
    ERROR_USER_NOT_EXIST(28, "User not exist", "用户不存在"),
    ERROR_UNSUPPORT_ORDER_TYPE(29, "Unsupport order type", "不支持的交易方式"),
    ERROR_ORDER_STATUS_INVALID(30, "Order status invalid", "订单状态异常"),
    ERROR_ORDER_NOT_EXIST(31,"Order not exist", "订单不存在"),

    ;
    int code;       // error code
    String enDes;   // english error description
    String chDes;   // chinese error description
    ErrorEnum(int code, String enDes, String chDes){
        this.chDes = chDes;
        this.code = code;
        this.enDes = enDes;
    }

    public int getCode() {
        return code;
    }

    public String getEnDes() {
        return enDes;
    }

    public String getChDes() {
        return chDes;
    }
}
