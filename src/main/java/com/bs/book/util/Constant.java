package com.bs.book.util;

public class Constant {
    // Session key
    public static final String Session_User_Key = "User";
    public static final String Session_Register_User_Key = "RegisterUser";
    public static final String Session_Forget_Password_Key = "ForgetPassword";

    // Chars toId generate code
    static final String CHARS = "1234567890abcdefghijklmnopqrstuvwxyz1" +
            "234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    // Book Aim
    public static final int Book_Aim_Sell = 0;
    public static final int Book_Aim_Buy = 1;

    // Book Status
    public static final int Book_Status_Origin = 0;
    public static final int Book_Status_Sold = 1;
    public static final int Book_Status_Handling = 2;  // 交易中

    // Order
    public static final int BOOK_ORDER_CREATE_TIME = 0;
    public static final int BOOK_ORDER_PRICE = 1;

    // Type
    public static final int ORDER_TYPE_MAIL = 0;
    public static final int ORDER_TYPE_OFFLINE = 1;

    // Order Action
    public static final int ORDER_ACTION_CREATE = 0;
    public static final int ORDER_ACTION_ACCEPT = 1;
    public static final int ORDER_ACTION_REFUSE = 2;
    public static final int ORDER_ACTION_RECEIVE = 3;
    public static final int ORDER_ACTION_SEND = 4;
    public static final int ORDER_ACTION_SUCCESS = 5;
    public static final int ORDER_ACTION_FAIL = 6;
    public static final int ORDER_ACTION_CANCEL = 7;

    // Message Type
    public static final int MESSAGE_TYPE_PERSON_TO_PERSON = 0;
    public static final int MESSAGE_TYPE_ORDER_CREATED = 1;
    public static final int MESSAGE_TYPE_ORDER_ACCEPTED = 2;
    public static final int MESSAGE_TYPE_ORDER_REFUSE = 3;
    public static final int MESSAGE_TYPE_ORDER_CANCEL = 4;
    public static final int MESSAGE_TYPE_ORDER_SEND = 5;
    public static final int MESSAGE_TYPE_ORDER_RECEIVE = 6;
    public static final int MESSAGE_TYPE_ORDER_OFFLINE_SUCCESS = 7;
    public static final int MESSAGE_TYPE_ORDER_OFFLINE_FAIL = 8;
}
