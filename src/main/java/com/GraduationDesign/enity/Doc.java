package com.GraduationDesign.enity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName Doc.java
 * @Description TODO
 * @createTime 2020年03月13日 13:55
 */
@Data
public class Doc {

    public Doc(){

    }

    public Doc(Integer id, String name, Date createTime, Date updateTime){
        super();
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    private Integer id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date updateTime;
}
