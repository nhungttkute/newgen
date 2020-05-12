/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import com.google.gson.Gson;
import com.newgen.am.dto.Pagination;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SerializationUtils;
import org.bson.json.JsonWriterSettings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author nhungtt
 */
public class Utility {
    private static String className = "Utility";

    public static String lpad3With0(long id) {
        return String.format("%03d", id);
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
                .int64Converter((value, writer) -> writer.writeNumber(String.valueOf(value)))
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

    public static String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                return currentUserName;
            } else {
                return "anonymous";
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String genRedisKey(String input) {
        String secretKey = ConfigLoader.getMainConfig().getString(Constant.REDIS_KEY_SECRET_KEY);
        String hash = DigestUtils.sha256Hex(secretKey + input);
        return hash;
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

    public static String generateRandomPin() {
        return "123456";
    }

    public static String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else return null;
    }
    
    public static Pagination getPagination(HttpServletRequest request, int count) throws Exception {

        Pagination pagination = new Pagination();

        int limit = Constant.PAGINATION_DEFAULT_LIMIT;
        if (Utility.isNotNull(request) && Utility.isNotNull(request.getQueryString())) {
        	String queryParams = decode(request.getQueryString());
        	limit = RequestParamsParser.getLimit(queryParams);
        }
        int totalPages = (count%limit > 0) ? (count/limit + 1) : count/limit;
        pagination.setTotalRows(count);
        pagination.setTotalPages(totalPages);
        return pagination;
    }

    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }
    
    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    public static String encodeValue(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    public static String decode(String value) throws Exception {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
    }
    
    public static void setRedisUserInfo(RedisTemplate template, String accessToken, UserInfoDTO userInfo, long refId) {
        UserInfoDTO redisUserInfo = (UserInfoDTO) SerializationUtils.clone(userInfo);
        redisUserInfo.setWatchLists(null);
        redisUserInfo.setLayout(null);
        redisUserInfo.setTheme(null);
        redisUserInfo.setLanguage(null);
        redisUserInfo.setFontSize(0);
        String key = Utility.genRedisKey(accessToken);
        String value = new Gson().toJson(redisUserInfo);
        AMLogger.logMessage(className, "setRedisUserInfo", refId, "REDIS_SET: key=" + key + ", value=" + value);
        template.opsForValue().set(key, value);
    }
    
    public static UserInfoDTO getRedisUserInfo(RedisTemplate template, String accessToken, long refId) {
        String methodName = "getRedisUserInfo";
        try {
            String key = genRedisKey(accessToken);
            String value = (String) template.opsForValue().get(key);
            AMLogger.logMessage(className, methodName, refId, "REDIS_GET: key=" + key + ", value=" + value);
            return new Gson().fromJson(value, UserInfoDTO.class);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot get user info from redis", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public static double roundAvoid(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}
    
}
