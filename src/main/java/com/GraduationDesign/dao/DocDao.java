package com.GraduationDesign.dao;

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

    //获取文档内的数据
    List<Update> getUpdate(Integer doc);

    //查找文档id有没有对用的文档
    Update find_doc(Integer doc);
}
