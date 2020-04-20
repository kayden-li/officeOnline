package com.GraduationDesign.dao;

import com.GraduationDesign.enity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName UserDao.java
 * @Description TODO
 * @createTime 2020年03月13日 14:18
 */
@Repository
public interface UserDao {

    //用户登录
    User login(User user);

    //用户注册
    Boolean register(User user);

    //根据用户名查询用户
    User findOneUserByName(@Param("name") String name);

    //根据邮箱查询用户
    User findOneUserByEmail(@Param("email") String email);
}
