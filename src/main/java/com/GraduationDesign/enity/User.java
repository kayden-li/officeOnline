package com.GraduationDesign.enity;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName User.java
 * @Description TODO
 * @createTime 2020年03月13日 13:32
 */
@Data
public class User {



    public User() {
        // TODO Auto-generated constructor stub
    }

    public User(String userName, String passWord, String email, Date createTime) {
        super();
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
        this.createTime = createTime;
    }

    // ORM
    // 封裝原則
    private Integer id;

    private String userName;

    private String passWord;

    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;


}
