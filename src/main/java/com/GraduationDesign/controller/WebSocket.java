package com.GraduationDesign.controller;

import com.GraduationDesign.common.websocket.WebsocketConfig;
import com.GraduationDesign.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

//@Transactional
//为创建，删除添加事务支持
@ServerEndpoint(value = "/webSocket/{user}/{doc}/{code}", configurator = WebsocketConfig.class)
public class WebSocket {

    @Autowired
    DocService docService;

    //<userID，WebSocket>
    //用于记录用户对应的连接
    private Map<String, Session> client = new ConcurrentHashMap<>();
    private Session session;
    private String user;
    private String doc;
    private String sym = "\\(\\[,\\]\\)";
    private String sendSym = "([,])";
    //用于保存在线用户
    //<文档，Map<用户，连接>>
    private static final Map<String, Map<String, Session>> GROUPS = new ConcurrentHashMap<>();
    //用于记录用户在表格中所占的位置
    //<用户, 位置>
    private static final Map<String, String> POS = new ConcurrentHashMap<>();
    //用于记录用户所在的sheet
    //<用户, sheet>
    private static final Map<String, String> SHEET = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(@PathParam("user") Integer user,
                       @PathParam("doc") Integer doc,
                       @PathParam("code") String code,
                       Session ws_session,
                       EndpointConfig config) {

        //验证码校验
        String Scode = (String) config.getUserProperties().get("code");
        if(code.equals(Scode)) {

            this.user = user.toString();
            this.doc = doc.toString();
            this.session = ws_session;

            //获取同组成员
            this.client = GROUPS.get(this.doc);
            if(null == this.client) {
                this.client = new HashMap<>();
            }

            //将自己添加进该组
            this.client.put(user.toString(), this.session);
            //更新在线用户
            GROUPS.put(doc.toString(), this.client);
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
        this.client = GROUPS.get(doc);
        //从组中删除本用户
        this.client.remove(user);
        //获取本用户所在的sheet
        String sheet = SHEET.get(this.user);
        //向其他用户发送清除此位置占用的消息
        Iterator iterable = client.keySet().iterator();
        while(iterable.hasNext()){
            //获取其他用户id
            String id = (String) iterable.next();
            //跳过不在同一sheet的用户
            if(!sheet.equals(SHEET.get(id))){
                continue;
            }
            //获取其他用户的session
            Session item = client.get(id);
            synchronized (item) {
                item.getAsyncRemote().sendText( "remove" + sendSym + this.user + sendSym + POS.get(this.user));
            }
        }
        //更新在线用户
        GROUPS.put(this.doc, client);

        //把该用户对应的位置信息删除
        SHEET.remove(this.user);
        POS.remove(this.user);

    }

    @OnMessage
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    public void onMessage(String message) {
        //请求其他用户位置的消息
        if("get".equals(message)){
            getPos();
            return;
        }
        String[] datas = message.split(sym);
        //获得sheet的消息
        if("sheet".equals(datas[0])){
            SHEET.put(this.user, datas[1]);
            return;
        }
        //如果发送的消息含有位置信息
        if(datas.length >= 2){
            //取出位置信息
            String row = datas[0];
            String col = datas[1];

            POS.put(this.user , row + sendSym + col);
        }
        //向同组其他在线用户转发该消息
        sendMessageAll(message);
    }

    @OnError
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    public void onError(Throwable error) {
        error.printStackTrace();
        synchronized (session) {
            session.getAsyncRemote().sendText("抱歉，您的连接出现错误，请重新连接");
        }
    }

    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    private void sendMessageAll(String message) {
        this.client = GROUPS.get(doc);
        //遍历同组成员
        Iterator iterable = client.keySet().iterator();
        while(iterable.hasNext()){
            //获取当前用户所在的sheet
            String sheet = SHEET.get(this.user);
            //获取被遍历者的id
            String id = (String) iterable.next();

            //跳过不在同一sheet的用户
            if(!sheet.equals(SHEET.get(id))){
                continue;
            }
            Session item = client.get(id);
            //跳过自己
            if(this.session == item){
                continue;
            }
            synchronized (item) {
                try {
                    item.getBasicRemote().sendText(this.user + sendSym + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getPos(){
        this.client = GROUPS.get(doc);
        //获取本用户所在的sheet
        String sheet = SHEET.get(this.user);
        //发送其他人占用的位置
        Iterator iterable = this.client.keySet().iterator();
        while(iterable.hasNext()){
            //获取其他人的id
            String id = (String)iterable.next();
            //跳过不在同一sheet的用户
            if(!sheet.equals(SHEET.get(id))){
                continue;
            }
            String item = POS.get(id);
            //防止有用户进入后没有点击
            if(null == item){
                continue;
            }
            //向新用户发送
            synchronized (this.session) {
                try {
                    this.session.getBasicRemote().sendText(id + sendSym + item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
