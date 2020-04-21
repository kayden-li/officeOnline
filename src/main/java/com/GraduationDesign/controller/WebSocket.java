package com.GraduationDesign.controller;

import com.GraduationDesign.common.WebsocketConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

//@Transactional
//为创建，删除添加事务支持
@ServerEndpoint(value = "/webSocket/{user}/{doc}/{code}", configurator = WebsocketConfig.class)
public class WebSocket {
    //<userID，WebSocket>
    //用于记录用户对应的连接
    private Map<String, Session> client = new HashMap<>();
    private Session session;
    private String user;
    private String doc;
    //<docID，Map<userID，WebSocket>>
    //用于保存在线用户
    private static final Map<String, Map<String, Session>> groups = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam("user") Integer user,
                       @PathParam("doc") Integer doc,
                       @PathParam("code") String code,
                       Session ws_session,
                       EndpointConfig config) {

        this.user = user.toString();
        this.doc = doc.toString();
        this.session = ws_session;

        String Scode = (String) config.getUserProperties().get("code");
        if(code.equals(Scode)) {
            //获取同组成员
            this.client = groups.get(this.doc);
            if(null == this.client) {
                this.client = new HashMap<>();
            }
            //将自己添加进该组
            this.client.put(user.toString(), this.session);
            //更新在线用户
            groups.put(doc.toString(), this.client);
        }else{
            try {
                this.session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose() {
        //client暂存同组成员
        this.client = groups.get(doc);
        //从组中删除本用户
        this.client.remove(user);
        //更新在线用户
        groups.put(doc, client);
    }

    @OnMessage
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    public void onMessage(String message) {
        //1.验证数据格式
        //2.向数据库中添加消息
        //3.向同组其他在线用户转发该消息
        sendMessageAll(message);
    }

    @OnError
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    public void onError(Throwable error) {
        error.printStackTrace();
        session.getAsyncRemote().sendText("抱歉，您的连接出现错误，请重新连接");
    }

    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    public void sendMessageAll(String message) {
        this.client = groups.get(doc);
        for (Session item : client.values()) {
            //跳过自己
            if(this.session == item){
                continue;
            }
            item.getAsyncRemote().sendText(this.user + "," + message);
        }
    }
}
