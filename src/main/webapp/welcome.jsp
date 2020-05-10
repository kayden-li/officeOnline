<%--
  Created by IntelliJ IDEA.
  User: 10245
  Date: 2020/4/3
  Time: 10:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome</title>

    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body>
    <div class="container" style="margin-top: 30px">
        <div class="row">
            <div class="col-xs-3">
                <ul class="nav nav-pills nav-stacked">
                    <li class="active">
                        <a href="#login" data-toggle="tab">登录</a></li>
                    <li><a href="#register" data-toggle="tab">注册</a></li>
                </ul>
            </div>

            <div class="col-xs-9 tab-content">
                <ul class="list-group tab-pane fade in active" id="login" onkeydown="keyLogin()">
                    <a class="list-group-item active"> 登录 </a>
                    <div role="form" id="L_form">
                        <div class="form-group">
                            <label for="name" style="margin-top: 50px;" id="Ll_name">用户名</label>
                            <input type="text" class="form-control" id="name" placeholder="请输入用户名">
                        </div>
                        <div class="form-group">
                            <label for="password" style="top: 50px;" id="Ll_password">密码</label>
                            <input type="password" class="form-control" id="password" placeholder="请输入密码">
                        </div>
                        <button id="loginBtn" class="btn btn-default">提交</button>
                    </div>
                </ul>
                <ul class="list-group tab-pane fade" id="register" onkeydown="keyRegister()">
                    <a class="list-group-item active"> 注册 </a>
                    <div role="form" id="R_form">
                        <div class="form-group">
                            <label for="r_name" style="margin-top: 50px;" id="Lr_name">用户名</label>
                            <input type="text" class="form-control" id="r_name" name="r_name" placeholder="请输入用户名">
                        </div>
                        <div class="form-group">
                            <label for="r_password" style="top: 50px;" id="Lr_password">密码</label>
                            <input type="password" class="form-control" id="r_password" name="r_password" placeholder="请输入8位以上密码">
                        </div>
                        <div class="form-group">
                            <label for="rr_password" style="top: 50px;" id="Lrr_password">确认密码</label>
                            <input type="password" class="form-control" id="rr_password" placeholder="请再次输入密码">
                        </div>
                        <div class="form-group">
                            <label for="r_email" style="top: 50px;" id="Lr_email">邮箱</label>
                            <input type="email" class="form-control" id="r_email" name="r_email" placeholder="请输入邮箱">
                        </div>
                        <button id="registerBtn" class="btn btn-default">提交</button>
                    </div>
                </ul>
            </div>
        </div>
    </div>
</body>
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
    //回车事件
    function keyLogin() {
        if(event.keyCode == 13){
            $("#loginBtn").click()
        }
    }
    function keyRegister() {
        if(event.keyCode == 13){
            $("#registerBtn").click()
        }
    }
    //登录ajax
    function loginAjax(name, password){
        $.ajax({
            type:"post",
            url:"${pageContext.request.contextPath}/login",
            data:{
                name:name,
                password:password
            },
            dataType:"json",
            success:function(response){
                //登陆成功，跳转至功能页
                if(1 == response.status){
                    window.location.href = "${pageContext.request.contextPath}/excel/toexcel"
                }else if(0 == response.status){
                    alert(response.msg)
                }
            }
        })
    }

    //登录表单提交
    $("#loginBtn").click(function(){
        if(!toLVaild()){
            return
        }
        loginAjax($("#name").val(), $("#password").val())
    })
    //注册表单提交
    $("#registerBtn").click(function(){
        if(!toRVaild()){
            return
        }
        $.ajax({
            type:"post",
            url:"${pageContext.request.contextPath}/register",
            data:{
                r_name:$("#r_name").val(),
                r_password:$("#r_password").val(),
                r_email:$("#r_email").val()
            },
            dataType:"json",
            success:function(response){
                if(0 == response.status){
                    alert(response.msg)
                }else if(1 == response.status){
                    //注册成功，登录
                    loginAjax($("#r_name").val(), $("#r_password").val())
                }
            }
        })
    })
    //注册验证
    function toRVaild() {
        var name = $("#r_name").val()
        var password = $("#r_password").val()
        var rpassword = $("#rr_password").val()
        var email = $("#r_email").val()
        if("" == name || 0 >= name.length){
            $("#Lr_name").css("color", "red")
            return false
        }
        if("" == password || 8 >= password.length){
            $("#Lr_password").css("color", "red")
            return false
        }
        if(password != rpassword){
            $("#Lrr_password").css("color", "red")
            return false
        }
        //邮箱正则表达式判断
        var regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/
        if(!regEmail.test(email)){
            $("#Lr_email").css("color", "red")
            return false
        }
        return true;
    }
    //登陆验证
    function toLVaild() {
        var name = $("#name").val()
        var password = $("#password").val()
        if("" == name || 0 >= name.length){
            $("#Ll_name").css("color", "red")
            return false
        }
        if("" == password || 8 >= password.length){
            $("#Ll_password").css("color", "red")
            return false
        }
        return true;
    }
    //登录报错消除
    $("#name").focus(function () {
        $("#Ll_name").css("color", "black")
    })
    $("#password").focus(function () {
        $("#Ll_password").css("color", "black")
    })
    //注册报错消除
    $("#r_name").focus(function () {
        $("#Lr_name").css("color", "black")
    })
    $("#r_password").focus(function () {
        $("#Lr_password").css("color", "black")
    })
    $("#rr_password").focus(function () {
        $("#Lrr_password").css("color", "black")
    })
    $("#r_email").focus(function () {
        $("#Lr_email").css("color", "black")
    })
</script>
</html>
