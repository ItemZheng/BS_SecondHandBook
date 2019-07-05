package com.bs.book.service.email;

public class EmailUtil {
    // 邮箱模版
    public static String RECEIVER = "旧书交易网站用户";
    public static String SENDER = "旧书交易网站";

    public static String VERIFICATION_EMAIL_TEMPLATE = "欢迎注册旧书交易网站，您的邮箱验证码为：$VERIFICATION_CODE。验证码十分钟邮箱，如非本人操作，请忽略。";
    public static String VERIFICATION_CODE_KEY = "$VERIFICATION_CODE";
    public static String VERIFICATION_THEME = "旧书交易网站邮箱验证";

    public static String FORGET_PASSWORD_EMAIL_TEMPLATE = "您正处于密码找回操作，您的验证码为：$VERIFICATION_CODE。如非本人操作，请检查账户安全。";
    public static String FORGET_PASSWORD_CODE_KEY = "$VERIFICATION_CODE";
    public static String FORGET_PASSWORD_THEME = "旧书交易网站密码找回";
}
