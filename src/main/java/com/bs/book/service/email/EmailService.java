package com.bs.book.service.email;

import com.bs.book.dal.UserRepository;
import com.bs.book.domain.User;
import com.bs.book.util.ErrorEnum;
import com.bs.book.util.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class EmailService {
    @Resource
    JavaMailSender mailSender;

    @Resource
    UserRepository userDb;

    @Value("${spring.mail.username}")
    private String SENDER;

    public String buildEmailContent(String template, Map<String, String> argument) {
        for(String key : argument.keySet()){
            template = template.replace(key, argument.get(key));
        }
        return template;
    }


    public void sendEmail(String theme, String emailAddress, String content)throws ServiceException{
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER);
            message.setTo(emailAddress);
            message.setSubject(theme);
            message.setText(content);
            mailSender.send(message);
        } catch (MailException e){
            log.error("Send email error, email address: " + emailAddress + " Error: " + e.getMessage());
            throw new ServiceException(ErrorEnum.ERROR_SEND_EMAIL);
        }
        log.info("SUCCESS SEND " + content + " \nTO " + emailAddress);
    }

    public void validEmailAddress(String email)throws ServiceException {
        if(email == null){
            throw new ServiceException(ErrorEnum.ERROR_EMAIL_FORMAT_INVALID);
        }
        // Check @
        int index = email.indexOf('@');
        if(index == -1 || index == 0 || index == email.length() - 1){
            throw new ServiceException(ErrorEnum.ERROR_EMAIL_FORMAT_INVALID);
        }

        // check if exist
        if(exist(email)){
            throw new ServiceException(ErrorEnum.ERROR_EMAIL_EXISTS);
        }
    }

    public boolean exist(String email){
        // check if exist
        User user = userDb.findByEmailAndRemoved(email, false);
        return user != null && user.getEmail().equals(email);
    }
}
