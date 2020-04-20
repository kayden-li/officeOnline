package com.GraduationDesign.enity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName Update.java
 * @Description 每个excell格更新后用来传送的数据信息
 * @createTime 2020年03月13日 13:59
 */
@Data
public class Update {

    public Update(){

    }

    public Update(Integer doc, Integer user, String sheet, String position, String text) {
        this.doc = doc;
        this.user = user;
        this.sheet = sheet;
        this.position = position;
        this.text = text;
    }

    private Integer doc;

    private Integer user;

    private String sheet;

    private String position;

    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date updateTime;

}
