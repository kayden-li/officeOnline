<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

    <script language="JavaScript" src="util/dist/xlsx.full.min.js"></script>

</head>
<body>
<div class="container">
    <div class="row" style="margin-top: 25%; margin-left: 20%;">
        <div class="col-xs-7">
            <button class="btn btn-large btn-success btn-block" type="button" onclick="$('#file').click()">上传文件</button>
            <button class="btn btn-large btn-success btn-block" type="button" style="margin-top: 5%;" onclick="toEdit()">新建文件</button>
            <div>
                <input id="docId" class="input-large" style="margin-top: 7%;margin-left: 5%;" ></input>
                <button  class="btn btn-mini btn-success" type="button" style="margin-left: 5%;" onclick="joinExcel()">加入文档</button>
            </div>
        </div>
    </div>

</div>
<input style="display: none;" id="file" type='file' onchange='readFile(event)'>
<input style="display: none;" id="pagecontext" value="${pageContext.request.contextPath}">

<script language="JavaScript" src="index.js"></script>
</body>

</html>
