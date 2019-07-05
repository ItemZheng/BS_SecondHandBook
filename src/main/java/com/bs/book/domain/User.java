package com.bs.book.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user_account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String email;
    private String qq;
    private String wx;
    private String phone;
    private Date create_time;
    private Date modify_time;
    private boolean removed;
}
