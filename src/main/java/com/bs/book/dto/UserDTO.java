package com.bs.book.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String qq;
    private String wx;
    private String phone;
}
