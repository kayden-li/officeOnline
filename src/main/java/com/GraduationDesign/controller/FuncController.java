package com.GraduationDesign.controller;

import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName FuncController.java
 * @Description TODO
 * @createTime 2020年04月12日 15:10
 */
@RestController
@RequestMapping("/excel")
public class FuncController {

    @Autowired
    private DocService docService;

    @RequestMapping("/toexcel")
    public ModelAndView toDocPage(HttpServletRequest request){
        docService.toDocPage(request);
        return new ModelAndView("redirect:/index.jsp");
    }

    @RequestMapping("/upload")
    public HigherResponse upload(HttpServletRequest request, String jsonData){
        return docService.upload(request, jsonData);
    }

    @RequestMapping("/edit")
    public HigherResponse edit(HttpServletRequest request, @RequestParam(required = false) Integer doc){
        return docService.toEdit(request, doc);
    }
}
