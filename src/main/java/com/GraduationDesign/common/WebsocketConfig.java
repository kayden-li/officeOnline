package com.GraduationDesign.common;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName WebsocketConfig.java
 * @Description TODO
 * @createTime 2020年04月14日 12:19
 */
public class WebsocketConfig extends ServerEndpointConfig.Configurator {

    private static final String HttpSession = null;

    /* 修改握手,就是在握手协议建立之前修改其中携带的内容 */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*如果没有监听器,那么这里获取到的HttpSession是null*/
        HttpSession session = (HttpSession) request.getHttpSession();
        if (session != null) {
            sec.getUserProperties().put("code", session.getAttribute("code"));
        }
        super.modifyHandshake(sec, request, response);
    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        //这个对象说一下，貌似只有服务器是tomcat的时候才需要配置,具体我没有研究
        return new ServerEndpointExporter();
    }
}
