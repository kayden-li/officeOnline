package com.GraduationDesign.service.Impl;

import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.common.SimpleExcel;
import com.GraduationDesign.common.Verify;
import com.GraduationDesign.dao.DocDao;
import com.GraduationDesign.enity.Doc;
import com.GraduationDesign.enity.Update;
import com.GraduationDesign.enity.Upload;
import com.GraduationDesign.service.DocService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName DocServiceImpl.java
 * @Description TODO
 * @createTime 2020年04月12日 15:09
 */
@Service
public class DocServiceImpl implements DocService {

    @Autowired
    DocDao docDao;

    @Override
    public void toDocPage(HttpServletRequest request) {
        HttpSession session = request.getSession();
        //向本次会话中添加验证码
        session.setAttribute("code", (new Verify()).verifyCode());
    }

    @Override
    public HigherResponse upload(HttpServletRequest request, String data) {
        if(!Verify.verifyLogin(request)){
            return HigherResponse.noLogin();
        }
        Integer user = Verify.getUser(request);
        Upload upload = new Gson().fromJson(data, Upload.class);
        //获取文件名+"_"+时间
        String excelName = upload.getExcelName();
        //设置excel文件名
        upload.setExcelName(excelName);
        //上传文件信息
        docDao.upload(upload);

        //获取数据库内excel的id
        Integer id = docDao.searchIdByName(excelName);

        //获取其中的所有sheet
        List uploadBean = upload.getExcel();

        //用于存放这张表的所有update
        List<Update> updates = new ArrayList<>();

        //遍历sheet，并将其转换为update类型
        for(int i = 0; i<uploadBean.size(); i++){
            //将修改好的所有update集合
            updates.addAll(sheet_2_Update(id, user, (Upload.UploadBean) uploadBean.get(i)));
        }
        //上传update
        for (Update update : updates) {
            try {
                docDao.in_update(update);
            }catch (Exception e){
                e.printStackTrace();
                //此处有一条报错就会返回结果，可以改为把所有结果收集起来一起返回结果
                return HigherResponse.getResponseFailed("位置："+update.getPosition()+"已经存在，本条数据未上传成功");
            }
        }
        return HigherResponse.getResponseSuccess("上传成功", id);
    }

    public HigherResponse toEdit(HttpServletRequest request, Integer doc){
        //验证用户是否登录
        if(!Verify.verifyLogin(request)){
            return HigherResponse.noLogin();
        }
        Boolean flag = true;
        List<String> sheets = docDao.getAllSheetsByDoc(doc);
        //文档没有sheet
        if(sheets == null || sheets.size() == 0) {
            //没有文档
            if (doc == null) {
                //新建文档
                String docName = "new_" + System.currentTimeMillis();

                docDao.createDoc(docName);

                doc = docDao.searchIdByName(docName);
            }
            sheets = new ArrayList();
            sheets.add("Sheet1");
            sheets.add("Sheet2");
            sheets.add("Sheet3");

            flag = false;
            //将选择的sheet写入session中
            request.getSession().setAttribute("Ssheet", "Sheet1");
        }
        if(flag){
            //将sheet插入session中
            request.getSession().setAttribute("Ssheet", sheets.get(0));
        }
        for (int i = 0; i < sheets.size(); i++) {
            sheets.set(i, "\"" + sheets.get(i) + "\"");
        }
        //将sheet插入session中
        request.getSession().setAttribute("sheet", sheets);

        //将doc写入session中
        request.getSession().setAttribute("doc", doc);
        //返回跳转成功
        return HigherResponse.getResponseSuccess("/chat.jsp");
    }

    //获取一个sheet的所有update数据
    public HigherResponse<List<Update>> getData(HttpServletRequest request){
        //验证用户是否登录
        if(!Verify.verifyLogin(request)){
            return HigherResponse.noLogin();
        }
        //从session中取出sheet
        String sheet = (String) request.getSession().getAttribute("Ssheet");
        //从session中取出doc
        Integer doc = (Integer)request.getSession().getAttribute("doc");
        //验证用户是否选择sheet
        if(null == sheet || "".equals(sheet) ||
            null == doc || "".equals(doc)){
            return HigherResponse.getResponseFailed("连接出错，请重新连接");
        }

        //从数据库中取出sheet内容
        List<Update> datas = docDao.getUpdate(sheet, doc);

        return HigherResponse.getResponseSuccess(datas);
    }

    public HigherResponse update(HttpServletRequest request, Update update) {
        //验证用户是否登录
        Integer user = (Integer) request.getSession().getAttribute("user");
        if (user == null) {
            return HigherResponse.noLogin();
        }
        //验证用户是否选择文档
        Integer doc = (Integer) request.getSession().getAttribute("doc");
        if (null == doc || doc <= 0) {
            return HigherResponse.getResponseFailed("连接出错，请重试");
        }
        String sheet = (String) request.getSession().getAttribute("Ssheet");
        if (null == sheet || "".equals(sheet)) {
            return HigherResponse.getResponseFailed("连接出错，请重试");
        }
        //设置用户更新的文档
        update.setDoc(doc);
        //设置用户
        update.setUser(user);
        //设置用户更新的sheet
        update.setSheet(sheet);

        //是删除数据的操作
        if (null == update.getText() || "".equals(update.getText())) {
            if (!docDao.deleteUpdate(update)) {
                return HigherResponse.getResponseFailed("删除失败");
            }
        }
        //若不是添加数据，则更新数据
        Update isUpdate = docDao.getUpadateByPos(update);
        if(null != isUpdate) {
            //若插入、更新都不成功，则返回失败
            if (!docDao.update(update)) {
                return HigherResponse.getResponseFailed("更新失败");
            }
        }else{
            if(!docDao.in_update(update)){
                return HigherResponse.getResponseFailed("插入失败");
            }
        }
        return HigherResponse.getResponseSuccess();
    }

    public HigherResponse download(HttpServletRequest request, HttpServletResponse response){
        //验证用户是否登录
        Integer user = (Integer) request.getSession().getAttribute("user");
        if(user == null){
            return HigherResponse.noLogin();
        }
        //验证用户是否选择文档
        Integer doc = (Integer) request.getSession().getAttribute("doc");
        if(null == doc || doc<=0){
            return HigherResponse.getResponseFailed("连接出错，请重试");
        }
        //设置sheet
        SimpleExcel excel = new SimpleExcel();
        List<String> sheets = docDao.getAllSheetsByDoc(doc);
        excel.setSheets(sheets);
        //设置update
        List<Update> updates = new ArrayList<>();
        for (String sheet : sheets) {
            updates.addAll(docDao.getUpdate(sheet, doc));
        }
        excel.setCells(updates);
        //设置文件名
        Doc docs = docDao.find_doc(doc);
        String[] names = docs.getName().split("\\.");
        StringBuffer name = new StringBuffer("");
        for (int i = 0; i < names.length-1; ++i) {
            name.append(names[i]);
        }
        excel.setFileName(name.toString());
        excel.setType(names[names.length-1]);
        //设置下载头
        try {
            excel.setResponseExportExcelHeader(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //向response中写入excel
        excel.output(response);
        return HigherResponse.getResponseSuccess();
    }

    /**
     * @title sheet_2_Update
     * @description 将sheet内容转化为update内容
     * @author 李帆
     * @params [doc docID, user userID, uploadBean sheet]
     * @updateTime 2020/4/18 11:10
     * @return
     * @return: java.util.List<com.GraduationDesign.enity.Update>
     * @throws
     */
    private List<Update> sheet_2_Update(Integer doc, Integer user, Upload.UploadBean uploadBean){
        //获取表名
        String sheetName = uploadBean.getSheetName();
        //获取内容位置
        List sheet = uploadBean.getText();
        //新建一个存储update的list
        List updates = new ArrayList();
        for(Object e : sheet){
            //获取position和text
            String position = ((Upload.UploadBean.element)e).getPosition();
            String text = ((Upload.UploadBean.element)e).getDoc();
            //实例化新update并添加进集合中
            Update update = new Update(doc, user, sheetName, position, text);
            updates.add(update);
        }
        //返回所有的update
        return updates;
    }

}
