package com.GraduationDesign.controller;


import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName Welcome.java
 * @Description TODO
 * @createTime 2020年03月13日 14:09
 */
@RestController
public class Welcome {

    @Autowired
    private UserService userService;

    @RequestMapping("/welcome")
    public ModelAndView welcome(){
        return new ModelAndView("redirect:/welcome.jsp");
    }

    @RequestMapping("/login")
    public HigherResponse<?> login(HttpServletRequest request,
                         @RequestParam(required = true) String name,
                         @RequestParam(required = true) String password){
        return userService.login(request, name, password);
    }

    @RequestMapping("/register")
    public HigherResponse<String> register(HttpServletRequest request,
                                   @RequestParam(required = true) String r_name,
                                   @RequestParam(required = true) String r_password,
                                   @RequestParam(required = true) String r_email){
        return userService.register(request, r_name, r_password, r_email);
    }
}
