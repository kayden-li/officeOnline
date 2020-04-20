package com.GraduationDesign.service;

import com.GraduationDesign.common.HigherResponse;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName UserService.java
 * @Description TODO
 * @createTime 2020年03月13日 14:10
 */
public interface UserService {

    HigherResponse login(HttpServletRequest request, String name, String psw);

    HigherResponse<String> register(HttpServletRequest request, String r_name, String r_password, String r_email);


}
