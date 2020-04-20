package com.GraduationDesign.common;

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
}
