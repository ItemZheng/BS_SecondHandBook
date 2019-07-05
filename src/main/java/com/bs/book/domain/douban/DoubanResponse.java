package com.bs.book.domain.douban;

import lombok.Data;

@Data
public class DoubanResponse {
    String msg;
    int code;
    String request;
}
