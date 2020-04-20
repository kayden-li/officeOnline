package com.GraduationDesign.service.Impl;

import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.common.Verify;
import com.GraduationDesign.dao.DocDao;
import com.GraduationDesign.enity.Update;
import com.GraduationDesign.enity.Upload;
import com.GraduationDesign.service.DocService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
        Integer user = (Integer) request.getSession().getAttribute("user");
        if(user == null){
            return HigherResponse.noLogin();
        }
        Upload upload = new Gson().fromJson(data, Upload.class);
        //获取文件名+"_"+时间
        String excelName = upload.getExcelName()+"_"+System.currentTimeMillis();
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
        //用于暂存update，若上传出错，可根据它获得报错位置
        Update updateT = new Update();
        //上传update
        for (Update update : updates) {
            updateT = update;

            try {
                docDao.in_update(update);
            }catch (Exception e){
                e.printStackTrace();
                return HigherResponse.getResponseFailed("位置："+updateT.getPosition()+"已经存在，本条数据未上传成功");
            }
        }
        return HigherResponse.getResponseSuccess("上传成功", id);
    }

    public HigherResponse toEdit(HttpServletRequest request, Integer doc){
        //验证登录
        Integer user = (Integer) request.getSession().getAttribute("user");
        if(user == null){
            return HigherResponse.noLogin();
        }
        //获取数据库中的最新update
        if(null == doc){
            return HigherResponse.getResponseSuccess();
        }
        List<Update> updates = docDao.getNewUpdate(doc, user);
        //将updates写入session
        request.getSession().setAttribute("updates", updates);
        //返回跳转成功
        return HigherResponse.getResponseSuccess("/chat.jsp");
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
