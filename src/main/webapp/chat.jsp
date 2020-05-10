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

    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body>
    <h1>消息：</h1>
    <div style="background: lightgrey" id="message"></div>

    <%--excel--%>
    <div class="excel-include"></div>

    <%--sheet列表--%>
    <div id="sheetList" class="btn-group">

    </div>
</body>
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/sockjs-client/1.4.0/sockjs.min.js"></script>
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script>
    //设置websocket地址
    let path = location.host+ "${pageContext.request.contextPath}/"
    //设置数据
    let user = "${sessionScope.user}"
    let doc = "${sessionScope.doc}"
    let code = "${sessionScope.code}"
    let data = user + "/" + doc + "/" + code
    /*创建一个websocket实例*/
    let socket

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
        //向其他用户发送sheet
        socket.send("sheet([,])"+"${sessionScope.Ssheet}")
        //向其他用户发送连接消息
        socket.send("join")
        //打开后自动获取sheet
        getSheets()
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

    //获取sheets并显示
    function getSheets(){
        let sheetLine = $("#sheetList")
        let sheets = ${sessionScope.sheet}
        let innerStr = ""
        for(let i = 0; i < sheets.length; i++){
            if(sheets[i] == "${sessionScope.Ssheet}"){
                innerStr += "<i class='btn btn-mini' style='border-bottom: solid 1px gray;'>"+sheets[i]+"</i>"
            }else{
                innerStr += "<div class='btn btn-large btn-warning' onclick='changeSheet(this)'>"+sheets[i]+"</div>"
            }

        }
        sheetLine.html(innerStr)
    }

    function changeSheet(ele) {
        //向其他用户发送更改sheet的消息
        socket.send("sheet([,])" + ele.innerText)
        window.location.href = "${pageContext.request.contextPath}/excel/changeSheet?sheetName="+ele.innerText
    }

</script>
<script language="JavaScript">
    $(".excel-include").load("excel.jsp")
</script>
</html>
