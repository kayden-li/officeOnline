package com.GraduationDesign.service;

import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.enity.Update;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName DocService.java
 * @Description TODO
 * @createTime 2020年04月12日 15:09
 */
public interface DocService {

    //跳转至docPage页
    void toDocPage(HttpServletRequest request);

    //上传excel内容
    HigherResponse upload(HttpServletRequest request, String data);

    //跳转至编辑页
    HigherResponse toEdit(HttpServletRequest request, Integer doc);

    //获取sheet对应的数据
    HigherResponse getData(HttpServletRequest request);

    //上传更新数据
    HigherResponse update(HttpServletRequest request, Update update);

    //下载文件
    HigherResponse download(HttpServletRequest request, HttpServletResponse response);

}
