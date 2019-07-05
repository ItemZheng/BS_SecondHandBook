package com.bs.book.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
    String name;
    String password;
    String email;
    String qq;
    String wx;
    String phone;
    String verificationCode;
}
