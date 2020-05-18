package com.GraduationDesign.common;

import com.GraduationDesign.enity.Update;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName simpleExcel.java
 * @Description TODO
 * @createTime 2020年05月10日 16:47
 */
@Data
public class SimpleExcel {

    private final String match_row = "[1-9][0-9]*";
    private final String match_col = "[A-Z]*";

    private String fileName = "test.xls";
    //sheet名
    private List<String> sheets = null;
    //所有的cell
    private List<Update> cells = null;
    //是转换后的cells
    //一行对应的cell
    //<行，该行的cell>
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, List<Update>> rows = new HashMap<>();

    private String type = "xls";

    public void output(HttpServletResponse response){
        //向sheet内插入数据
        preSetCells();
        switch (type){
            case "xls":
                xlsOutput(response);
                break;
            case "xlsx":
                xlsxOutput(response);
                break;
        }
    }

    private void xlsOutput(HttpServletResponse response){
        //在内存中创建一个excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建工作簿
        for (String name : this.sheets) {
            HSSFSheet sheet = wb.createSheet(name);
            Iterator iterator = this.rows.keySet().iterator();
            while(iterator.hasNext()){
                //行号
                String rowNum = (String)iterator.next();
                //实际的行
                HSSFRow row = sheet.createRow(Integer.valueOf(rowNum));
                //该行需要插入的数据
                List<Update> cells = this.rows.get(rowNum);
                for (Update cell : cells) {
                    if(!cell.getSheet().equals(name)){
                        continue;
                    }
                    //将数据写入文件中
                    row.createCell(Integer.valueOf(cell.getPosition())).setCellValue(cell.getText());
                }
            }
        }
        OutputStream outputStream = null;
        try {
            //获取输出流对象
            outputStream = response.getOutputStream();
            //写出
            wb.write(outputStream);
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void xlsxOutput(HttpServletResponse response){
        //在内存中创建一个excel文件
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建工作簿
        for (String name : this.sheets) {
            XSSFSheet sheet = wb.createSheet(name);
            Iterator iterator = this.rows.keySet().iterator();
            while(iterator.hasNext()){
                //行号
                String rowNum = (String)iterator.next();
                //实际的行
                XSSFRow row = sheet.createRow(Integer.valueOf(rowNum));
                //该行需要插入的数据
                List<Update> cells = this.rows.get(rowNum);
                for (Update cell : cells) {
                    if(!cell.getSheet().equals(name)){
                        continue;
                    }
                    //将数据写入文件中
                    row.createCell(Integer.valueOf(cell.getPosition())).setCellValue(cell.getText());
                }
            }
        }
        OutputStream outputStream = null;
        try {
            //获取输出流对象
            outputStream = response.getOutputStream();
            //写出
            wb.write(outputStream);
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //设置消息头
    public HttpServletResponse setResponseExportExcelHeader(HttpServletResponse response) throws UnsupportedEncodingException {
        Assert.notNull(response);
        Assert.notNull(this.fileName+"."+this.type);
        String s = new String((this.fileName+"."+this.type).getBytes(), "ISO8859-1");
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + s);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        return response;
    }

    /**
     * @title ABC2Num
     * @description 将excel的列号转换为数字
     * @author 李帆
     * @params [a：待转换字符串]
     * @updateTime 2020/5/10 17:21
     * @return
     * @return: java.lang.Integer
     * @throws
     */
    public static Integer ABC2Num (String a){
        String[] str = a.toLowerCase().split("");
        Integer al = str.length;
        Integer numout = 0;
        Integer charnum = 0;
        for(int i = 0; i < al; i++){
            charnum = str[i].charAt(0) - 96;
            numout += charnum * (int)Math.pow(26, al-i-1);
        }
        return numout;
    }

    /**
     * 将数字转换为字母
     * @param columnIndex 列号
     * @return
     */
    public static String excelColIndexToStr(int columnIndex) {
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

    private void preSetCells(){
        for (Update cell : this.cells) {
            //先取好行列
            Integer row = 0;
            Integer col = 0;
            //在位置中正则匹配
            Pattern r = Pattern.compile(match_row);
            Matcher m = r.matcher(cell.getPosition());
            if(m.find()){
                row = Integer.valueOf(m.group());
                row -= 1;
            }
            r = Pattern.compile(match_col);
            m = r.matcher(cell.getPosition());
            if(m.find()){
                col = Integer.valueOf(ABC2Num(m.group()));
                col -= 1;
            }

            //向对应的rowCell中添加该update
            List<Update> cells = this.rows.get(row.toString());
            if(null == cells || cells.size() == 0){
                cells = new ArrayList<>();
            }
            //位置用于保存列
            cell.setPosition(col.toString());
            cells.add(cell);
            this.rows.put(row.toString(), cells);

        }
    }

}
