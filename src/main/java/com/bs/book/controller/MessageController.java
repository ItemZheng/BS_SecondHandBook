package com.bs.book.controller;

import com.bs.book.dal.MessageRepository;
import com.bs.book.domain.Message;
import com.bs.book.util.Constant;
import com.bs.book.util.Util;
import com.bs.book.util.WebSocketKeyUserIdMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/message")
public class MessageController extends BaseController{
    @Resource
    MessageRepository messageDb;

    @RequestMapping("/send")
    public Object send(long to, String msg){
        Message message = buildMessage(getUser().getId(), to, msg);
        MessageClient.sendMessage(message, false);
        messageDb.save(message);
        return buildSuccessResp(null);
    }

    @RequestMapping("/key")
    public Object setKey(){
        String key = Util.generateRamdonCode(64);
        WebSocketKeyUserIdMap.keyUserIdMap.put(key, getUser().getId());
        log.info("WebSocketKey Of " + getUser().getId() + ": " + key);
        return buildSuccessResp(key);
    }

    // 上线之后获取未读消息
    @RequestMapping("/getMessage")
    public Object getMessage(){
        ArrayList<Message> messages =
                messageDb.getAllByToIdAndRemovedOrderByCreateTimeAsc(getUser().getId(), false);
        // map
        if(messages != null){
            MessageClient.sendMessages(messages);
            messageDb.saveAll(messages);
        }
        return buildSuccessResp(null);
    }

    // 获取更多消息
    @RequestMapping("/getMoreMessage")
    public Object getMoreMessage(long fromMessageId, long anotherUser){
        ArrayList<Message> messages1 = getMessages(fromMessageId, getUser().getId(), anotherUser);
        ArrayList<Message> messages2 = getMessages(fromMessageId, anotherUser, getUser().getId());

        // get all messages
        ArrayList<Message> allMessages = null;
        if(messages1 == null){
            allMessages = messages2;
        } else if(messages2 == null){
            allMessages = messages1;
        } else {
            messages1.addAll(messages2);
            allMessages = messages1;
        }

        if(allMessages != null){
            allMessages.sort(new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    if (o1.getId() < o2.getId()) {
                        return -1;
                    } else if (o1.getId() > o2.getId()) {
                        return 1;
                    }
                    return 0;
                }
            });
            if(allMessages.size() >= 40){
                allMessages = (ArrayList<Message>)allMessages.subList(allMessages.size() - 40, allMessages.size() - 1);
            }
        }
        return buildSuccessResp(allMessages);
    }

    private ArrayList<Message> getMessages(long fromMessageId, long from, long to){
        if(fromMessageId == -1){
            return messageDb.getTop40ByToIdAndFromIdAndRemovedOrderByCreateTimeAsc(to, from, false);
        } else {
            return messageDb.getTop40ByToIdAndFromIdAndRemovedAndIdIsLessThanOrderByCreateTimeAsc(to,
                    from, false, fromMessageId);
        }
    }

    private Message buildMessage(long from, long to, String msg){
        Message message = new Message();
        message.setFromId(from);
        message.setToId(to);
        message.setMsg(msg);
        message.setType(Constant.MESSAGE_TYPE_PERSON_TO_PERSON);
        message.setOrderId(0);
        message.setReadStatus(false);
        message.setRemoved(false);
        // time
        Date timeNow = new Date();
        message.setCreateTime(timeNow);
        message.setModifyTime(timeNow);
        return message;
    }

}
