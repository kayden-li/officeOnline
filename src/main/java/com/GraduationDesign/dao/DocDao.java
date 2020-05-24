package com.GraduationDesign.dao;

import com.GraduationDesign.enity.Doc;
import com.GraduationDesign.enity.Update;
import com.GraduationDesign.enity.Upload;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName DocDao.java
 * @Description TODO
 * @createTime 2020年04月16日 15:02
 */
@Repository
public interface DocDao {

    //上传
    Boolean upload(Upload upload);

    //通过文件名获取文档id
    Integer searchIdByName(String name);

    //插入源文件内的数据
    Boolean in_update(Update update);

    //获取一个文档所有的sheet名
    List<String> getAllSheetsByDoc(Integer doc);

    //获取文档内的数据
    List<Update> getUpdate(String sheet, Integer doc);

    //获取某一位置的数据
    Update getUpadateByPos(Update update);

    //删除某一位置的内容
    Boolean deleteUpdate(Update update);

    //查找文档id有没有对用的文档
    Doc find_doc(Integer doc);

    //新建文档
    Boolean createDoc(String docName);

    //更新update
    Boolean update(Update update);
}
