package com.bs.book.util;

public class ServiceException extends Exception {
    ErrorEnum errorEnum;

    public ServiceException(ErrorEnum errorEnum){
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum(){
        return errorEnum;
    }
}
