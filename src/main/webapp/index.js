var excel = new Object();
var sheet = new Object();
var row = new Object();
var cell = new Object();
var x ;
var wb;
var output;
var filename;
var rows_a,rows_b;
var cols_a,cols_b;
var range = new Object();

var data = new Object()

var pagecontext = $("#pagecontext").val()

var readFile = function(event) {
    var read = new FileReader();
    var file = event.target.files[0];
    read.readAsBinaryString(file);
    filename = file.name;
    read.onload = function (event){
        //res即为输入的文件内容，但文件流不对
        var res = event.currentTarget.result;
        var fileType = getFileType(filename).toString();
        switch (fileType){
            case '.xls':
                x = XLS;
                break;
            case '.xlsx':
                x = XLSX;
                break;
            default:
                x = null;
        }
        if(x) {
            //以字节流读取的文件内容，里面是文件的所有相关信息
            wb = x.read(res, {type: 'binary'});
            var excelTemp = new Array()
            for (var i = 0; i < wb.SheetNames.length; i++) {
                var element = new Array()
                for (text in wb.Sheets[wb.SheetNames[i]]) {
                    if (text == '!ref' || text == '!margins') {
                        continue
                    }
                    //一张表的数据
                    element.push({'position': text, 'doc': wb.Sheets[wb.SheetNames[i]][text].v})
                }
                excelTemp.push({"SheetName":wb.SheetNames[i],"text":element})
            }
            data['excel'] = excelTemp
            excel = getEXCEL(wb);
            output = excel;
        }else{
            var info =  {error:'can not resolve file end with :'+fileType};
            output = getOutput(info,'obj');
        }
        //console.log(output);
        data['excelName'] = output.excelName
        uploadExcel(data)
        /*out_json =  getOutput(output,'json');
        $('#debug').val(out_json);*/

    };

};

function getEXCEL(wb){
    var Sheetnames = wb.SheetNames;
    var Sheets = new Object();
    var Sheet = new Object();

    Sheets = wb.Sheets;
    excel['sheetSize'] = Sheetnames.length.toString();
    excel['excelName'] = filename;
    excel["sheets"] = [];


    for(var i=0;i<Sheetnames.length;i++){
        Sheet = Sheets[Sheetnames[i]];
        range = getRanger(Sheet['!ref']);
        rows_a = range.row;
        cols_a = range.col;
        var m = 0;
        sheet = new Object();
        sheet['rowSize'] = rows_a.toString();
        sheet['colSize'] = cols_a.toString();
        sheet['sheetName'] = Sheetnames[i];
        sheet["rows"] = [];
        sheet["title"] = new Object();
        for(var j=0;j<rows_a;j++){
            row = new Object();
            row['cells'] = [];
            for(var m=0;m<cols_a;m++){
                cell = new Object();
                row['cells'].push(cell);
            }
            sheet["rows"].push(row);
        }
        for(k in Sheet){
            if(isCell(k)){
                range = getRanger(k);
                rows_b = range.row.toString();
                cols_b = range.col.toString();
                row = sheet["rows"][rows_b-1];
                row.val = null;
                row.val = 'true';
                cell = row['cells'][cols_b-1];
                cell.value = null;
                cell.value = Sheet[k].w;
            }
        }
        for(t in sheet["rows"]){
            var tit = sheet["rows"][t];
            if(tit.val){
                sheet["title"] = sheet["rows"][t];
                break;
            }
        }
        excel["sheets"].push(sheet);
    }
    return excel;
}

function isCell(str){
    var reg = /^[a-zA-Z]+[0-9]+$/;
    return reg.test(str);
}

function getRowNumber(key){
    var reg = /[0-9]+$/;
    var rownumber = reg.exec(key);
    return rownumber;
}

function getColLetter(key){
    var reg = /^[a-zA-Z]+/;
    var colletter = reg.exec(key);
    return colletter;
}

function getColNumber(key){
    var reg = /[a-zA-Z]+/;
    var colletter = reg.exec(key);
    return colletter;
}

//改变输出类型
function getOutput(obj,type){
    switch (type){
        case 'json' :
            return JSON.stringify(obj);
            break;
        case 'obj'  :
            return obj;
            break;
        default :
            return JSON.stringify(obj);
    }
}

//获取内容范围
function getRanger(ref){
    if(ref == undefined){
        range.row = "0";
        range.col = "0";
    }else{
        var reg = /:[a-zA-Z]+[0-9]+/;
        var reg2 = /^[a-zA-Z]+[0-9]+$/;
        var pos = reg.exec(ref);
        if(pos == undefined ){
            pos = reg2.exec(ref);
        }
        var r = getRowNumber(pos).toString();
        var codes = [];
        var n;
        var letter = getColNumber(pos).toString();
        letter = letter.toLocaleLowerCase();
        n = 0;
        for(i in letter){
            var code = letter[i].charCodeAt()-96;
            codes[i] = code;
            n++;
        }
        n--;
        var mathstr = 0;
        for(j in codes){
            mathstr = 'Math.pow(26,'+n+')*'+codes[j] + '+'+ mathstr;
            n--;
        }
        var    c = eval(mathstr).toString();
        range.row = r;
        range.col = c;
    }
    return range;
}

//获取文件类型
function getFileType(filepath){
    var reg = /.[a-zA-Z0-9]+$/;
    var filePostfix = reg.exec(filepath);
    return filePostfix;
}

//上传excel文件
function uploadExcel(jsonObj){
    $.ajax({
        type:"post",
        url:pagecontext+"/excel/upload",
        data:{
            jsonData:JSON.stringify(data),
        },
        dataType:"json",
        success:function(response){
            console.log(response)
            //未登录
            if(response.status == 10){
                alert(response.msg)
                window.location.href = pagecontext+"/welcome"
            }else if(response.status == 0){
                //上传未成功
                alert(response.data)
            }else if(response.status == 1){
                //上传成功
                alert("上传成功，进入编辑页面")
                toEdit(response.data)
            }
        }
    })
}

//进入编辑页面
function toEdit(id){
    $.ajax({
        type:"post",
        url:pagecontext+"/excel/edit",
        data:{
            doc:id,
        },
        dataType:"json",
        success:function(response){
            console.log(response)
            //未登录
            if(response.status == 10){
                alert(response.data)
                window.location.href = pagecontext+"/welcome"
            }else if(response.status == 0){
                //服务器出错
                alert(response.data)
            }else if(response.status == 1){
                //上传成功
                window.location.href = pagecontext + response.msg
            }
        }
    })
}