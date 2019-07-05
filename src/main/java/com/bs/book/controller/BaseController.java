package com.bs.book.controller;

import com.bs.book.domain.User;
import com.bs.book.domain.WebResponse;
import com.bs.book.dto.ForgetPasswordDTO;
import com.bs.book.dto.RegisterUserDTO;
import com.bs.book.util.Constant;
import com.bs.book.util.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

public class BaseController {
    @Autowired
    private HttpSession session;

    // provide basic method toId build response
    Object buildSuccessResp(Object data){
        return new WebResponse(0, "Success", data);
    }

    Object buildResponse(int code, String msg, Object data){
        return new WebResponse(code, msg, data);
    }

    Object buildResponse(ErrorEnum errorEnum){
        if(errorEnum == null){
            errorEnum = ErrorEnum.ERROR_UNKNOWN;
        }
        return new WebResponse(errorEnum.getCode(), errorEnum.getEnDes() + "(" + errorEnum.getChDes() + ")", null);
    }

    User getUser(){
        return (User)session.getAttribute(Constant.Session_User_Key);
    }

    void setUser(User user){
        session.setAttribute(Constant.Session_User_Key, user);
    }

    void setRegisterUser(RegisterUserDTO registerUserDTO){
        session.setAttribute(Constant.Session_Register_User_Key, registerUserDTO);
    }

    RegisterUserDTO getRegisterUser(){
        return (RegisterUserDTO)session.getAttribute(Constant.Session_Register_User_Key);
    }

    void setForgetPasswordDTO(ForgetPasswordDTO forgetPasswordDTO){
        session.setAttribute(Constant.Session_Forget_Password_Key, forgetPasswordDTO);
    }

    ForgetPasswordDTO getForgetPasswordDTO(){
        return (ForgetPasswordDTO)session.getAttribute(Constant.Session_Forget_Password_Key);
    }
}
