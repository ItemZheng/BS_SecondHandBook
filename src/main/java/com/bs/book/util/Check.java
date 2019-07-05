package com.bs.book.util;

public class Check {
    public static void validPasswordFormat(String password) throws ServiceException{
        // 密码为 6 ～20 个字符
        if(password == null){
            throw new ServiceException(ErrorEnum.ERROR_PASSWORD_FORMAT_INVALID);
        }

        if(password.length() >= 6 && password.length() <= 20){
            return;
        }
        throw new ServiceException(ErrorEnum.ERROR_PASSWORD_FORMAT_INVALID);
    }
}
