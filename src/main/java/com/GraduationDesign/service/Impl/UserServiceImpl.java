package com.GraduationDesign.service.Impl;

import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.dao.UserDao;
import com.GraduationDesign.enity.User;
import com.GraduationDesign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName UserServiceImpl.java
 * @Description TODO
 * @createTime 2020年03月13日 14:12
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public HigherResponse login(HttpServletRequest request, String name, String psw) {

        User user = new User(name, psw, null, null);
        User userAct = userDao.login(user);
        if(null == userAct) {
            return HigherResponse.getResponseFailed("登陆失败");
        } else {
            //将登录用户的id写入session中
            request.getSession().setAttribute("user", userAct.getId());
            return HigherResponse.getResponseSuccess("登陆成功");
        }
    }

    @Override
    public HigherResponse<String> register(HttpServletRequest request, String r_name, String r_password, String r_email) {

        User testN = userDao.findOneUserByName(r_name);
        if(null != testN){
            return HigherResponse.getResponseFailed("注册失败，用户名已被使用");
        }

        User testE = userDao.findOneUserByEmail(r_email);
        if(null != testE){
            return HigherResponse.getResponseFailed("注册失败，邮箱已被使用");
        }

        User user = new User(r_name, r_password,r_email,null);
        Boolean result = userDao.register(user);
        String msg;
        if(result){
            msg = "注册成功";
        }else{
            msg = "注册失败";
        }
        return HigherResponse.getResponseSuccess(msg);
    }

}
