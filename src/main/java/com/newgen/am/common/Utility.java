/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import com.google.gson.Gson;
import com.newgen.am.dto.ActivityLogDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.json.JsonWriterSettings;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public static double getDouble(Double obj) {
        if (isNull(obj)) {
            return 0;
        }
        return obj;
    }

    public static int getInt(Integer obj) {
        if (isNull(obj)) {
            return 0;
        }
        return obj;
    }

    public static JsonWriterSettings getJsonWriterSettings() {
        JsonWriterSettings settings = JsonWriterSettings.builder()
                .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                .booleanConverter((value, writer) -> writer.writeBoolean(value.booleanValue()))
                .build();
        return settings;
    }

    public static List<SimpleGrantedAuthority> getInvestorAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("INVESTOR"));
    }

    public static List<SimpleGrantedAuthority> getAdminAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ADMIN"));
    }

    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }
        return null;
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
    
    public static String generateRandomPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
          .concat(numbers)
          .concat(specialChar)
          .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
          .mapToObj(c -> (char) c)
          .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
          .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
          .toString();
        return password;
    }
}
