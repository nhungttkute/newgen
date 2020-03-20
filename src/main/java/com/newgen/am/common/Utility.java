/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.newgen.am.dto.ActivityLogDTO;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author nhungtt
 */
public class Utility {

    public static List<SimpleGrantedAuthority> getAuthority() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT"));
    }

    public static String lpadWith0(long id) {
        return String.format("%010d", id);
    }

    public static boolean isNull(Object obj) {
        return obj == null || "".equals(obj);
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static String convertObjectToJson(Object obj) {
        String json = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            json = mapper.writeValueAsString(obj);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static String genRedisKey(String input) {
        String secretKey = ConfigLoader.getMainConfig().getString(Constant.REDIS_KEY_SECRET_KEY);
        String hash = DigestUtils.sha256Hex(secretKey + input);
        return hash;
    }
    
    public static void logActivity(String orgType, String orgCode, String userId, String username, String action, String accessToken, String note) throws Exception {
        LocalServiceConnection serviceCon = new LocalServiceConnection();
        ActivityLogDTO activityLog = new ActivityLogDTO();
        activityLog.setOrgType(orgType);
        activityLog.setOrgCode(orgCode);
        activityLog.setUserId(userId);
        activityLog.setUsername(username);
        activityLog.setAction(action);
        activityLog.setAccessToken(accessToken);
        activityLog.setNote(note);
        serviceCon.sendPostRequest(serviceCon.getActivityLogServiceURL(), new Gson().toJson(activityLog));
    }
}
