package com.GraduationDesign.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * * <b>Description:</b><br>
 * 
 * @author 李帆
 * @version 1.0
 * @Note <b>ProjectName:</b> 20191225_ <br>
 *       <b>PackageName:</b> com.neusoft.util <br>
 *       <b>ClassName:</b> HigherResponse <br>
 *       <b>Date:</b> 2019年12月27日 上午11:26:01 <br>
 *       <b>Description:</b> 封装高复用的响应对象
 */
@JsonInclude(value = Include.NON_NULL)
@Data
public class HigherResponse<T> {

    private HigherResponse() {

    }

    private HigherResponse(Integer status) {
        this.status = status;
    }

    private HigherResponse(Integer status, T t) {
        this.status = status;
        this.data = t;
    }

    private HigherResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private HigherResponse(Integer status, String msg, T t) {
        this.status = status;
        this.msg = msg;
        this.data = t;
    }

    private Integer status;

    private T data;

    private String msg;

    /**
     * 提供对外公开的方法
     */

    // 判断是否成功
    @JsonIgnore
    public boolean isResponseSuccess() {
        return this.status == StatusUtil.SUCCESS_STATUS;
    }

    // Success
    public static HigherResponse getResponseSuccess() {
        return new HigherResponse(StatusUtil.SUCCESS_STATUS);
    }

    public static HigherResponse getResponseSuccess(String msg) {
        return new HigherResponse(StatusUtil.SUCCESS_STATUS, msg);
    }

    public static <T> HigherResponse getResponseSuccess(T t) {
        return new HigherResponse(StatusUtil.SUCCESS_STATUS, t);
    }

    public static <T> HigherResponse getResponseSuccess(String msg, T t) {
        return new HigherResponse(StatusUtil.SUCCESS_STATUS, msg, t);
    }

    // 判断是否失败
    @JsonIgnore
    public boolean isResponseFailed() {
        return this.status == StatusUtil.FAILED_STATUS;
    }

    // Failed
    public static HigherResponse getResponseFailed() {
        return new HigherResponse(StatusUtil.FAILED_STATUS);
    }

    public static HigherResponse getResponseFailed(String msg) {
        return new HigherResponse(StatusUtil.FAILED_STATUS, msg);
    }

    // 用户未登录
    public static HigherResponse noLogin() {
        return new HigherResponse(StatusUtil.NO_LOGIN, "用户未登录,请登录");
    }

    // 用户已登录
    public static HigherResponse isLogin() {
        return new HigherResponse<>(StatusUtil.IS_LOGIN, "用户已登录");
    }
}
