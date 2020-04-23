package com.GraduationDesign.controller;

import com.GraduationDesign.common.WebsocketConfig;

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
    //<userID，WebSocket>
    //用于记录用户对应的连接
    private Map<String, Session> client = new ConcurrentHashMap<>();
    private Session session;
    private String user;
    private String doc;
    //<docID，Map<userID，WebSocket>>
    //用于保存在线用户
    private static final Map<String, Map<String, Session>> groups = new ConcurrentHashMap<>();
    //用于记录用户在表格中所占的位置
    private static final Map<String, String> pos = new ConcurrentHashMap<>();

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

        //向其他用户发送清楚此位置的消息

        //把该用户对应的位置信息删除
        pos.remove(user);
    }

    @OnMessage
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    public void onMessage(String message) {
        if("get".equals(message)){
            getPos();
            return;
        }
        String[] datas = message.split(",");
        //如果发送的消息含有位置信息
        if(datas.length >= 2){
            //取出位置信息
            String row = datas[0];
            String col = datas[1];
            //将位置信息保存在pos中
            pos.put(this.user , row + "," + col);
        }
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
        synchronized (session) {
            session.getAsyncRemote().sendText("抱歉，您的连接出现错误，请重新连接");
        }
    }

    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    //该方法不需要事务支持
    private void sendMessageAll(String message) {
        this.client = groups.get(doc);
        Iterator iterable = client.keySet().iterator();
        while(iterable.hasNext()){
            String id = (String) iterable.next();
            Session item = client.get(id);
            //跳过自己
            if(this.session == item){
                continue;
            }
            synchronized (item) {
                item.getAsyncRemote().sendText(this.user + "," + message);
            }
        }
    }

    private void getPos(){
        //发送其他人占用的位置
        Iterator iterable = this.client.keySet().iterator();
        while(iterable.hasNext()){
            //获取其他人的位置
            String id = (String)iterable.next();
            String item = this.pos.get(id);
            //防止有用户进入后没有点击
            if(null == item){
                continue;
            }
            //向新用户发送
            synchronized (this.session) {
                try {
                    this.session.getBasicRemote().sendText(id + "," + item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
