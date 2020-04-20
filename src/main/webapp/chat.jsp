<%--
  Created by IntelliJ IDEA.
  User: 李帆
  Date: 2020/4/11
  Time: 11:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>消息：</h1>
    <div style="background: lightgrey" id="message"></div>

    <div id="excel-include"></div>
</body>
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/sockjs-client/1.4.0/sockjs.min.js"></script>
<script language="JavaScript">
    $("#excel-include").load("excel.html")
</script>
<script>
    //设置websocket地址
    var path = location.host+ "${pageContext.request.contextPath}/"
    //设置测试数据
    var user = "${sessionScope.user}"
    var doc = 1
    var code = "${sessionScope.code}"
    var data = user + "/" + doc + "/" + code
    /*创建一个websocket实例*/
    var socket

    /*
     * ws开头：websocket协议
     */
    if('WebSocket' in window){
        socket = new WebSocket("ws://"+ path + "webSocket/" + data)
    }else if('MozWebSocket' in window){
        socket = new MozWebSocket("ws://" + path + "webSocket/" + data)
    }else{
        socket = new SockJS("http://" + path + "webSocket/sockjs/" + data)
    }

    //连接发生错误的回调方法
    socket.onerror = function() {
        alert("网络连接发生错误")
        setMessageInnerHTML("网络连接发生错误");
    }
    /*自动调用的回调函数：*/
    /*打开socket*/
    socket.onopen = function (ev) {
        setMessageInnerHTML("网络连接成功")
        socket.send("${sessionScope.user}"+"进入本文档")
    }
    /*监听消息*/
    socket.onmessage = function (ev) {
        setMessageInnerHTML(ev.data)
        //socket.close();
    }

    /*客户端关闭连接*/
    /*socket.close()*/
    /*监听关闭连接事件*/
    socket.onclose = function (ev) {
        setMessageInnerHTML("网络连接关闭")
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function() {
        closeWebSocket();
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        socket.close();
    }
    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        $("#message").html("网络连接成功<br/>"+innerHTML + '<br/>')
    }


</script>
</html>
