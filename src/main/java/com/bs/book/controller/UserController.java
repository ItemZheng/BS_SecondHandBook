package com.bs.book.controller;

import com.bs.book.annotation.LoginIgnore;
import com.bs.book.dal.UserRepository;
import com.bs.book.domain.User;
import com.bs.book.dto.ForgetPasswordDTO;
import com.bs.book.dto.RegisterUserDTO;
import com.bs.book.dto.UserDTO;
import com.bs.book.service.email.EmailService;
import com.bs.book.service.email.EmailUtil;
import com.bs.book.service.user.UserService;
import com.bs.book.util.Check;
import com.bs.book.util.ErrorEnum;
import com.bs.book.util.ServiceException;
import com.bs.book.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController extends BaseController {
    @Resource
    UserService userService;

    @Resource
    EmailService emailService;

    @Resource
    UserRepository userDb;

    @RequestMapping("/register")
    @LoginIgnore
    public Object register(@NotNull String name, @NotNull String password,
                          @NotNull String email, String qq, String wx, String phone) {
        try {
            // check username
            userService.validUserName(name, false);
            // check password
            Check.validPasswordFormat(password);
            // check email
            emailService.validEmailAddress(email);
            // send email
            String verificationCode = Util.generateRamdonCode(6);
            Map<String, String> arguments = new HashMap<>();
            arguments.put(EmailUtil.VERIFICATION_CODE_KEY, verificationCode);
            String content = emailService.buildEmailContent(EmailUtil.VERIFICATION_EMAIL_TEMPLATE, arguments);
            emailService.sendEmail(EmailUtil.VERIFICATION_THEME, email, content);
            // record
            RegisterUserDTO registerUserDTO = new RegisterUserDTO();
            registerUserDTO.setName(name);
            registerUserDTO.setPassword(password);
            registerUserDTO.setEmail(email);
            registerUserDTO.setQq(qq);
            registerUserDTO.setWx(wx);
            registerUserDTO.setPhone(phone);
            registerUserDTO.setVerificationCode(verificationCode);
            setRegisterUser(registerUserDTO);
            // success
            return buildSuccessResp(null);
        }catch (ServiceException e){
            log.warn("Register Error: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }

    }

    @RequestMapping("/verify")
    @LoginIgnore
    public Object verify(@NotNull String code){
        RegisterUserDTO registerUserDTO = getRegisterUser();
        if(registerUserDTO == null){
            return buildResponse(ErrorEnum.ERROR_NOT_REGISTER);
        }
        // check code
        if(!code.equals(registerUserDTO.getVerificationCode())){
            return buildResponse(ErrorEnum.ERROR_VERIFICATION_CODE_NOT_MATCH);
        }

        try {
            userService.addUser(registerUserDTO);
        }catch (ServiceException e){
            log.warn("Verify Error: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }

        log.info("SUCCESS INSERT USER " + registerUserDTO.getName());
        return buildSuccessResp(null);
    }


    @RequestMapping("/login")
    @LoginIgnore
    public Object login(@NotNull String username, @NotNull String key) {
        User user = userDb.findByNameAndRemoved(username, false);
        if(user == null || !user.getName().equals(username)){
            return buildResponse(ErrorEnum.ERROR_USERNAME_NOT_EXIST);
        }
        try {
            if(!key.equals(user.getPassword())){
                return buildResponse(ErrorEnum.ERROR_PASSWORD_ERROR);
            }
            setUser(user);
            return buildSuccessResp(null);
        } catch (Exception e){
            log.error("LOGIN ERROR " + e.getMessage() + " INFO " + username + user.getPassword());
            return buildResponse(ErrorEnum.ERROR_UNKNOWN);
        }
    }

    @RequestMapping("/info")
    public Object info(){
        User user = getUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setQq(user.getQq());
        userDTO.setWx(user.getWx());
        return buildSuccessResp(userDTO);
    }

    @RequestMapping("/queryById")
    public Object queryById(long id){
        User user = userDb.getOne(id);
        if(user == null){
            return buildResponse(ErrorEnum.ERROR_USER_NOT_EXIST);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPhone(user.getPhone());
        userDTO.setQq(user.getQq());
        userDTO.setWx(user.getWx());
        return buildSuccessResp(userDTO);
    }

    @RequestMapping("/updateInfo")
    public Object updateInfo(String qq, String wx, String phone){
        User user = getUser();
        user.setQq(qq);
        user.setWx(wx);
        user.setPhone(phone);
        user.setModify_time(new Date());
        try {
            userService.updateInfo(user);
            setUser(user);
        }catch (ServiceException e){
            log.warn("Update Error: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
        return buildSuccessResp(null);
    }

    @RequestMapping("/updateUsername")
    public Object updateUsername(String username){
        User user = getUser();
        user.setName(username);
        user.setModify_time(new Date());
        try {
            userService.updateUsername(user);
            setUser(user);
        }catch (ServiceException e){
            log.warn("UpdateUsername Error: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }
        return buildSuccessResp(null);
    }

    @RequestMapping("/updatePassword")
    public Object updatePassword(@NotNull String key, @NotNull String newPassword){
        User user = getUser();
        // check password
        try{
            if(user.getPassword().equals(newPassword)){
                return buildResponse(ErrorEnum.ERROR_SAME_PASSWORD);
            }
            // check old password
            if(!key.equals(user.getPassword())){
                return buildResponse(ErrorEnum.ERROR_PASSWORD_ERROR);
            }
            // check new password
            Check.validPasswordFormat(newPassword);
            user.setPassword(newPassword);
            user.setModify_time(new Date());
            userService.update(user);
            return buildSuccessResp(null);
        }catch (ServiceException e){
            log.warn("UpdatePassword Error: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        } catch (Exception e){
            log.error("UPDATE PASSWORD ERROR " + e.getMessage() + " Username: " + user.getName());
            return buildResponse(ErrorEnum.ERROR_UNKNOWN);
        }
    }

    @RequestMapping("/forgetPassword")
    @LoginIgnore
    public Object forgetPassword(String email){
        // check send frequency
        ForgetPasswordDTO forgetPasswordDTO = getForgetPasswordDTO();
        if(forgetPasswordDTO != null && forgetPasswordDTO.getLastSendTime().getTime() + 2 * 60 * 1000 > new Date().getTime()){
            return buildResponse(ErrorEnum.ERROR_EMAIL_SEND_TOO_FREQUENTLY);
        }

        // check email
        if(!emailService.exist(email)){
            return buildResponse(ErrorEnum.ERROR_EMAIL_NOT_EXISTS);
        }

        // send email
        String verificationCode = Util.generateRamdonCode(6);
        Map<String, String> arguments = new HashMap<>();
        arguments.put(EmailUtil.FORGET_PASSWORD_CODE_KEY, verificationCode);
        String content = emailService.buildEmailContent(EmailUtil.FORGET_PASSWORD_EMAIL_TEMPLATE, arguments);
        try {
            emailService.sendEmail(EmailUtil.VERIFICATION_THEME, email, content);
        } catch (ServiceException e){
            log.warn("ForgetPassword Error: " + e.getErrorEnum().getEnDes());
            return buildResponse(e.getErrorEnum());
        }

        // send successfully
        ForgetPasswordDTO f = new ForgetPasswordDTO();
        f.setEmail(email);
        f.setVerificationCode(verificationCode);
        f.setLastSendTime(new Date());
        setForgetPasswordDTO(f);
        return buildSuccessResp(null);
    }

    @RequestMapping("resetForgotPassword")
    @LoginIgnore
    public Object resetForgotPassword(String verificationCode, String newPassword){
        ForgetPasswordDTO forgetPasswordDTO = getForgetPasswordDTO();
        if(forgetPasswordDTO == null){
            return buildResponse(ErrorEnum.ERROR_SEND_EMAIL_FIRST);
        }

        // check verificationCode
        if(!forgetPasswordDTO.getVerificationCode().equals(verificationCode)){
            return buildResponse(ErrorEnum.ERROR_VERIFICATION_CODE_NOT_MATCH);
        }

        try {
            Check.validPasswordFormat(newPassword);
            User user = userService.getUserByEmail(forgetPasswordDTO.getEmail());
            user.setPassword(newPassword);
            user.setModify_time(new Date());
            userService.update(user);
            return buildSuccessResp(null);
        } catch (ServiceException e){
            return buildResponse(e.getErrorEnum());
        }
    }
}
