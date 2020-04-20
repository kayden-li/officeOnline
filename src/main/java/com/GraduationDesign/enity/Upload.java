package com.GraduationDesign.enity;

import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author 李帆
 * @version 1.0.0
 * @ClassName Upload.java
 * @Description 用于解析上传的文档中的json数据
 * @createTime 2020年04月16日 14:33
 */
public class Upload {

    private String excelName;
    private List<UploadBean> excel;

    public class UploadBean{
        private String SheetName;
        private List<element> text;

        public class element{
            private String position;
            private String doc;

            public String getPosition() {
                return position;
            }

            public String getDoc() {
                return doc;
            }
            @Override
            public String toString(){
                return "p:"+position+"v:"+doc;
            }
        }

        public String getSheetName() {
            return SheetName;
        }

        public List<element> getText() {
            return text;
        }

        public String getTextStr(){
            StringBuffer str = new StringBuffer("");
            for(element e : text){
                str.append(e.toString());
            }
            return str.toString();
        }
    }

    public List<UploadBean> getExcel() {
        return excel;
    }

    public String getExcelName() {
        return excelName;
    }

    public void setExcelName(String name) {
        this.excelName = name;
    }
}
