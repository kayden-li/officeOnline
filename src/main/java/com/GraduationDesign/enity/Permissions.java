package com.GraduationDesign.enity;

import lombok.Data;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName Permissions.java
 * @Description TODO
 * @createTime 2020年03月13日 13:50
 */
@Data
public class Permissions {

    public Permissions(){

    }

    public Permissions(Integer doc, Integer user, Integer enable){
        super();
        this.doc = doc;
        this.user = user;
        this.enable = enable;
    }

    private Integer doc;
    private Integer user;
    private Integer enable;
}
