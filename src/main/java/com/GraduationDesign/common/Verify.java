package com.GraduationDesign.common;

import com.GraduationDesign.enity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName Verify.java
 * @Description TODO
 * @createTime 2020年04月14日 11:09
 */
public class Verify {
    private int VERIFY_CODE_LENGTH = 6;

    public String verifyCode() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < VERIFY_CODE_LENGTH; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static Boolean verifyLogin(HttpServletRequest request){
        Integer user = (Integer) request.getSession().getAttribute("user");
        if(user == null){
            return false;
        }
        return true;
    }
    public static Integer getUser(HttpServletRequest request){
        return (Integer) request.getSession().getAttribute("user");
    }
    public static Boolean verifyDoc(HttpServletRequest request){
        Integer doc = (Integer) request.getSession().getAttribute("doc");
        if(null == doc || doc<=0){
            return false;
        }
        return true;
    }
    public static Integer getDoc(HttpServletRequest request){
        return (Integer) request.getSession().getAttribute("doc");
    }
}
