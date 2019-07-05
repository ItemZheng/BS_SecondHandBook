package com.bs.book.service.user;

import com.bs.book.dal.UserRepository;
import com.bs.book.domain.User;
import com.bs.book.dto.RegisterUserDTO;
import com.bs.book.service.email.EmailService;
import com.bs.book.util.Check;
import com.bs.book.util.ErrorEnum;
import com.bs.book.util.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class UserService {
    @Resource
    UserRepository userDb;

    @Resource
    EmailService emailService;

    public void validUserName(String username, Boolean allowExist)throws ServiceException {
        // 用户名为 1 ～20 个字符
        if(username == null){
            throw new ServiceException(ErrorEnum.ERROR_USERNAME_FORMAT_INVALID);
        }

        // Check length
        if(username.length() < 1 || username.length() > 20){
            throw new ServiceException(ErrorEnum.ERROR_USERNAME_FORMAT_INVALID);
        }
        if(!allowExist){
            // check if exist
            if(exist(username)){
                throw new ServiceException(ErrorEnum.ERROR_USERNAME_EXISTS);
            }
        }
    }

    public void addUser(RegisterUserDTO registerUserDTO)throws ServiceException{
        // check email
        emailService.validEmailAddress(registerUserDTO.getEmail());
        // check password
        Check.validPasswordFormat(registerUserDTO.getPassword());

        // check username
        validUserName(registerUserDTO.getName(), false);

        // new User
        User user = new User();
        user.setName(registerUserDTO.getName());
        user.setEmail(registerUserDTO.getEmail());
        user.setPassword(registerUserDTO.getPassword());
        user.setPhone(registerUserDTO.getPhone());
        user.setQq(registerUserDTO.getQq());
        user.setWx(registerUserDTO.getWx());
        user.setRemoved(false);
        Date timeNow = new Date();
        user.setCreate_time(timeNow);
        user.setModify_time(timeNow);
        saveUser(user);
    }

    public void updateInfo(User user)throws ServiceException{
        saveUser(user);
    }

    public void update(User user)throws ServiceException{
        saveUser(user);
    }

    public void updateUsername(User user)throws ServiceException{
        validUserName(user.getName(), false);
        saveUser(user);
    }

    private void saveUser(User user) throws ServiceException{
        try {
            userDb.saveAndFlush(user);
        } catch (Exception e){
            log.error("UPDATE USER ERROR. USER " + user.getName() + " ERROR " + e.getMessage());
            throw new ServiceException(ErrorEnum.ERROR_SAVE_USER_FAIL);
        }
    }

    public User getUserByEmail(String email){
        User user = userDb.findByEmailAndRemoved(email, false);
        if(user != null && user.getEmail().equals(email)){
            return user;
        }
        return null;
    }

    public boolean exist(String username){
        User user = userDb.findByNameAndRemoved(username, false);
        return user != null && user.getName().equals(username);
    }
}
