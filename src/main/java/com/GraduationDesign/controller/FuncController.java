package com.GraduationDesign.controller;

import com.GraduationDesign.common.HigherResponse;
import com.GraduationDesign.enity.Update;
import com.GraduationDesign.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @RequestMapping("/changeSheet")
    public ModelAndView changeSheet(HttpServletRequest request,String sheetName){
        List<String> sheets = (List<String>)request.getSession().getAttribute("sheet");
        StringBuffer sb = new StringBuffer("\""+sheetName+"\"");
        if(sheets.contains(sb.toString())) {
            request.getSession().setAttribute("Ssheet", sheetName);
        }
        return new ModelAndView("redirect:/chat.jsp");

    }

    @RequestMapping("/data")
    public HigherResponse data(HttpServletRequest request){
        return docService.getData(request);
    }

    @RequestMapping("/update")
    public HigherResponse update(HttpServletRequest request, String str){
        Update update = new Update();
        String[] datas = str.split("\\(\\[,\\]\\)");
        //位置转换及设置
        StringBuffer sb = new StringBuffer(excelColIndexToStr(Integer.valueOf(datas[1])));
        sb.append(datas[0]);
        update.setPosition(sb.toString());
        //设置更新文本
        update.setText(datas[2]);
        return docService.update(request, update);
    }

    /**
     * 将数字转换为字母
     * @param columnIndex 列号
     * @return
     */
    private String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }

}
