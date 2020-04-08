/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 *
 * @author nhungtt
 */
public class FileUtility {

    public final static String CREATE_NEW_USER_EMAIL_FILE = "CREATE_NEW_USER_EMAIL_FILE";
    public final static String CREATE_NEW_USER_EMAIL_SUBJECT = "MXV M-System - Thông báo tạo mới user đăng nhập thành công";
    public final static String CHANGE_PASSWORD_EMAIL_FILE = "CHANGE_PASSWORD_EMAIL_FILE";
    public final static String CHANGE_PASSWORD_EMAIL_SUBJECT = "MXV M-System - Thông báo thay đổi thành công mật khẩu user đăng nhập";

    private String className = "FileUtility";

    public String loadFileContent(String fileName, long refId) {
        String methodName = "loadFileContent";
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF8"));
            // read line by line
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return content.toString();
    }
}
