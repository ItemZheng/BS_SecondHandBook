package com.bs.book.controller;

import com.bs.book.domain.Message;
import com.bs.book.util.WebSocketKeyUserIdMap;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ServerEndpoint("/websocket/{userId}")
@Slf4j
public class MessageClient extends BaseController{
    private long userId;
    private Session session;
    // map
    private static Map<Long, MessageClient> clients = new HashMap<>();

    @OnOpen
    public void onOpen(@PathParam("userId") long userId, Session session){
        this.userId = userId;
        this.session = session;
        log.info("USER " + userId + " CONNECT");

        // add toId map
        clients.put(userId, this);
    }


    @OnClose
    public void OnClose(){
        clients.remove(userId);
        log.info("USER " + userId + "DISCONNECT");
    }

    public static void sendMessage(Message message){
        sendMessage(message, true);
    }

    public static void sendMessage(Message message, boolean once){
        // try toId send
        if(clients.containsKey(message.getToId())){
            // get if online
            MessageClient client = getClient(message.getToId());
            if(client != null){
                client.session.getAsyncRemote().sendText(new Gson().toJson(message));
                message.setReadStatus(true);
            }
        }
        if(!once){
            // try fromId send
            if(clients.containsKey(message.getFromId())){
                // get if online
                MessageClient client = getClient(message.getFromId());
                if(client != null){
                    client.session.getAsyncRemote().sendText(new Gson().toJson(message));
                }
            }
        }
    }

    static void sendMessages(List<Message> messages){
        if(messages != null){
            for(Message message: messages){
                sendMessage(message, true);
            }
        }
    }

    private static MessageClient getClient(long userId){
        // get if online
        MessageClient client = clients.get(userId);
        if(client == null){
            return null;
        }
        if(!client.session.isOpen()){
            return null;
        }
        return client;
    }

}
