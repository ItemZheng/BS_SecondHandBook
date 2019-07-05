package com.bs.book.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ForgetPasswordDTO {
    String email;
    String verificationCode;
    Date lastSendTime;
}
