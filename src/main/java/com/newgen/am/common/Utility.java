/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.WordUtils;
import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.EmailListmsisdnDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.NotifyServiceDTO;
import com.newgen.am.dto.Pagination;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Investor;

/**
 *
 * @author nhungtt
 */
public class Utility {
    private static String className = "Utility";

    private static Gson gson = new Gson();
    
    public static Gson getGson() {
    	return gson;
    }
    public static String lpad3With0(long id) {
        return String.format("%03d", id);
    }
    
    public static String lpad5With0(long id) {
        return String.format("%05d", id);
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

    public static long getLong(Long obj) {
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

    public static long convertStringToLong(String str) {
    	try {
    		return Long.parseLong(str);
    	} catch (Exception e) {
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
    
    public static String generateSpecialChars(int iLength) {
    	final String alphabet = "!#$%*@";
    	final int N = alphabet.length();
    	Random rd = new Random();
    	StringBuilder sb = new StringBuilder(iLength);
    	for (int i = 0; i < iLength; i++) {
    	    sb.append(alphabet.charAt(rd.nextInt(N)));
    	}
    	return sb.toString();
    }

    public static String generateRandomPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
//        String specialChar = RandomStringUtils.random(2, 33, 42, false, false);
        String specialChar = generateSpecialChars(2);
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
    
    public static boolean isLocalRequest(HttpServletRequest request) {
        String secretKey = getAccessToken(request);
        if (ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY).equalsIgnoreCase(secretKey)) return true;
        return false;
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
        redisUserInfo.setTableSetting(null);
        redisUserInfo.setWatchLists(null);
        redisUserInfo.setLayout(null);
        redisUserInfo.setTheme(null);
        redisUserInfo.setLanguage(null);
        redisUserInfo.setFontSize(0);
        String key = Utility.genRedisKey(accessToken);
        String value = gson.toJson(redisUserInfo);
        template.opsForValue().set(key, value, Duration.ofDays(1));
    }
    
    public static UserInfoDTO getRedisUserInfo(RedisTemplate template, String accessToken, long refId) {
        String methodName = "getRedisUserInfo";
        try {
            String key = genRedisKey(accessToken);
            AMLogger.logMessage(className, methodName, refId, "REDIS_GET: key=" + key);
            String value = (String) template.opsForValue().get(key);
            return gson.fromJson(value, UserInfoDTO.class);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot get user info from redis", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public static void deleteOldRedisUserInfo(RedisTemplate<String, String> template, String accessToken, long refId) {
    	try {
    		String key = Utility.genRedisKey(accessToken);
    		AMLogger.logMessage(className, "deleteOldRedisUserInfo", refId, "REDIS_DELETE: key=" + key);
    		template.delete(key);
    	} catch (Exception e) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
		
	}
    
    public static <T> void setRedisInfo(RedisTemplate<String, String> template, String key, T object, long refId) {
    	try {
    		String value = gson.toJson(object);
        	AMLogger.logMessage(className, "setRedisInfo", refId, "REDIS_SET: key=" + key + ", value=" + value);
            template.opsForValue().set(key, value);
    	} catch (Exception e) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    public static void deleteRedisInfo(RedisTemplate<String, String> template, String key, long refId) {
    	try {
    		AMLogger.logMessage(className, "deleteRedisInfo", refId, "REDIS_DELETE: key=" + key);
    		template.delete(key);
    	} catch (Exception e) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
	}
    
    public static double roundAvoid(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}
    
    public static boolean checkExistedAdminEmail(String email) {
    	// check in admin email exists
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> loginAdminUser = database.getCollection("login_admin_users");
		
		Document query = new Document();
        query.append("$and", Arrays.asList(
                new Document()
                        .append("username", new Document()
                                .append("$not", new BsonRegularExpression(".*TVKD.*", "i"))
                        ),
                new Document()
                        .append("username", new Document()
                                .append("$not", new BsonRegularExpression(".*MG.*", "i"))
                        ),
                new Document()
                        .append("username", new Document()
                                .append("$not", new BsonRegularExpression(".*CTVMG.*", "i"))
                        ),
                new Document()
                        .append("email", email)
            )
        );
		
		long investorCount = loginAdminUser.countDocuments(query);
		
		return (investorCount > 0) ? true : false;
    }
    
    public static boolean checkExistedMemberCode(String memberCode) {
    	// check in member code exists
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> memberCollection = database.getCollection("members");
		
		Document memberQuery = new Document();
		memberQuery.append("code", memberCode);
		
		long memberCount = memberCollection.countDocuments(memberQuery);
		
		return (memberCount > 0) ? true : false;
    }
    
    public static boolean checkExistedInvestorCode(String investorCode) {
    	// check in member code exists
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> investorCollection = database.getCollection("investors");
		
		Document investorQuery = new Document();
		investorQuery.append("investorCode", investorCode);
		
		long investorCount = investorCollection.countDocuments(investorQuery);
		
		return (investorCount > 0) ? true : false;
    }
    
    public static boolean checkExistedBrokerCode(String brokerCode) {
    	// check in member code exists
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("brokers");
		
		Document query = new Document();
		query.append("code", brokerCode);
		
		long count = collection.countDocuments(query);
		
		return (count > 0) ? true : false;
    }
    
    public static boolean checkExistedMemberTaxCode(String taxCode) {
    	// check in members
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> memberCollection = database.getCollection("members");
		
		Document memberQuery = new Document();
		memberQuery.append("company.taxCode", taxCode);
		
		long memberCount = memberCollection.countDocuments(memberQuery);
		return (memberCount > 0) ? true : false;
    }
    
    public static boolean checkExistedInvestorTaxCode(String memberCode, String taxCode) {
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> investorCollection = database.getCollection("investors");

		Document invQuery = new Document();
		invQuery = new Document();
		invQuery.append("memberCode", memberCode);
		invQuery.append("company.taxCode", taxCode);
		
		long invCount = investorCollection.countDocuments(invQuery);
		
		return (invCount > 0) ? true : false;
    }
    
    public static boolean checkExistedInvestorTaxCode2(String investorCode, String taxCode) {
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> investorCollection = database.getCollection("investors");

		// get memberCode
		String memberCode = investorCode.substring(0,3);
		// count taxCode
		Document invQuery = new Document();
		invQuery = new Document();
		invQuery.append("memberCode", memberCode);
		invQuery.append("company.taxCode", taxCode);
		
		long invCount = investorCollection.countDocuments(invQuery);
		
		return (invCount > 0) ? true : false;
    }
    
    public static boolean checkExistedInvestorIdentityCard2(String investorCode, String identityCard) {
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> investorCollection = database.getCollection("investors");
		
		// get memberCode
		String memberCode = investorCode.substring(0,3);
		// count identityCard
		Document invQuery = new Document();
		invQuery = new Document();
		invQuery.append("memberCode", memberCode);
		invQuery.append("individual.identityCard", identityCard);
		
		long invCount = investorCollection.countDocuments(invQuery);
		return (invCount > 0) ? true : false;
    }
    
    public static boolean checkExistedInvestorIdentityCard(String memberCode, String identityCard) {
    	MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> investorCollection = database.getCollection("investors");
		
		Document invQuery = new Document();
		invQuery.append("memberCode", memberCode);
		invQuery.append("individual.identityCard", identityCard);
		
		long invCount = investorCollection.countDocuments(invQuery);
		
		return (invCount > 0) ? true : false;
    }
    
    public static List<String> getNumberQueryFieldNames() {
    	List<String> fieldNames = new ArrayList<String>();
    	fieldNames.add("createdDate");
    	fieldNames.add("approvalDate");
    	return fieldNames;
    }
    
    public static boolean isInvestorCompany(String type) {
    	boolean flag = false;
    	if (Constant.INVESTOR_TYPE_INNER_TRADING_COMPANY.equals(type) || Constant.INVESTOR_TYPE_LOCAL_COMPANY.equals(type) || Constant.INVESTOR_TYPE_FOREIGN_COMPANY.equals(type)) {
    		flag = true;
    	}
    	return flag;
    }
    
    public static boolean isInvestorIndividual(String type) {
    	boolean flag = false;
    	if (Constant.INVESTOR_TYPE_LOCAL_INDIVIDUAL.equals(type) || Constant.INVESTOR_TYPE_FOREIGN_INDIVIDUAL.equals(type)) {
    		flag = true;
    	}
    	return flag;
    }
    
    public static String getClassName(String input) {
    	String[] arr = input.split("_");
    	if (arr.length > 0) {
    		return arr[0];
    	}
    	return null;
    }
    
    public static String getMethodName(String input) {
    	String[] arr = input.split("_");
    	if (arr.length > 0) {
    		return arr[1];
    	}
    	return null;
    }
    
    public static String getServiceName(String input) {
    	String[] arr = input.split("\\.");
    	if (arr.length > 0) {
    		String str = arr[arr.length - 1];
    		return WordUtils.uncapitalize(str);
    	}
    	return null;
    }
    
    public static boolean isDeptUser(UserInfoDTO userInfo) {
    	if (isNotNull(userInfo.getDeptCode())) return true;
    	return false;
    }
    
    public static boolean isMemberUser(UserInfoDTO userInfo) {
    	if (isNotNull(userInfo.getMemberCode()) && isNull(userInfo.getBrokerCode()) && isNull(userInfo.getCollaboratorCode()) && isNull(userInfo.getInvestorCode())) return true;
    	return false;
    }
 
    public static boolean isBrokerUser(UserInfoDTO userInfo) {
    	if (isNotNull(userInfo.getBrokerCode()) && isNull(userInfo.getCollaboratorCode()) && isNull(userInfo.getInvestorCode())) return true;
    	return false;
    }
    
    public static boolean isCollaboratorUser(UserInfoDTO userInfo) {
    	if (isNotNull(userInfo.getCollaboratorCode()) && isNull(userInfo.getInvestorCode())) return true;
    	return false;
    }
    
    public static boolean isInvestorUser(UserInfoDTO userInfo) {
    	if (isNotNull(userInfo.getInvestorCode())) return true;
    	return false;
    }
    
    public static void sendHandleLogout(List<String> usernameList, long refId) {
    	String methodName = "sendHandleLogout";
		try {
			NotifyServiceDTO notifyDto = new NotifyServiceDTO();
			notifyDto.setUserID(usernameList);
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			serviceCon.sendPostRequest(serviceCon.getLogoutHandleServiceURL(), gson.toJson(notifyDto), null);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    public static String getJsonFieldValue(String jsonFieldName, String resp) {
        jsonFieldName = "\"" + jsonFieldName + "\"";
        String jsonFieldValue = null;
        int index = resp.indexOf(jsonFieldName);
        if (index != -1) {
            int startIndex = resp.indexOf("\"", index + jsonFieldName.length());
            int endIndex = resp.indexOf("\"", startIndex + 1);
            jsonFieldValue = resp.substring(startIndex + 1, endIndex);
        }
        return jsonFieldValue;
    }
    
    public static void sendChangePasswordEmail(String toEmail, String username, String password, long refId) {
		String methodName = "sendChangePasswordEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setSettingType(Constant.SERVICE_NOTIFICATION_SETTING_TYPE_RESET_PASSWORD);
			email.setSendingObject(Constant.SERVICE_NOTIFICATION_SENDING_OBJ);
			
			EmailListmsisdnDTO listmsisdn = new EmailListmsisdnDTO();
			listmsisdn.setEmails(Arrays.asList(toEmail));
			
			email.setListmsisdn(listmsisdn);
			email.setSubject(FileUtility.CHANGE_PASSWORD_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CHANGE_PASSWORD_EMAIL_FILE), refId),
					username, password);
			email.setBodyStr(emailBody);
			String emailJson = gson.toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    public static void sendChangePINEmail(String toEmail, String username, String pin, long refId) {
		String methodName = "sendChangePINEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setSettingType(Constant.SERVICE_NOTIFICATION_SETTING_TYPE_RESET_PIN);
			email.setSendingObject(Constant.SERVICE_NOTIFICATION_SENDING_OBJ);
			
			EmailListmsisdnDTO listmsisdn = new EmailListmsisdnDTO();
			listmsisdn.setEmails(Arrays.asList(toEmail));
			
			email.setListmsisdn(listmsisdn);
			email.setSubject(FileUtility.CHANGE_PIN_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CHANGE_PIN_EMAIL_FILE), refId),
					username, pin);
			email.setBodyStr(emailBody);
			String emailJson = gson.toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
    public static boolean isProdMode() {
    	if("1".equals(ConfigLoader.getMainConfig().getString(Constant.PROD_FLAG))) return true;
    	return false;
    }
    
    public static boolean isNotifyOn() {
    	if("1".equals(ConfigLoader.getMainConfig().getString(Constant.NOTIFY_FLAG))) return true;
    	return false;
    }
    
    public static boolean isCQGSyncOn() {
    	if("1".equals(ConfigLoader.getMainConfig().getString(Constant.CQG_SYNC_FLAG))) return true;
    	return false;
    }
    
    public static boolean isCQGSyncBalanceOn() {
    	if("1".equals(ConfigLoader.getMainConfig().getString(Constant.CQG_SYNC_BALANCE_FLAG))) return true;
    	return false;
    }
    
    public static String getCQGAccountName(InvestorDTO investor) {
    	String accountName = "";
    	if(investor.getInvestorName().length() > 64) {
			accountName = investor.getInvestorName().substring(0, 64);
		} else {
			accountName = investor.getInvestorName();
		}
    	
    	return accountName;
    }
    
    public static String convertSessionDateFromLong(long timestamp) {
    	Date date = new Date(timestamp);
    	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    	return formater.format(date);
    }
    
    public static void sendCreateNewUserEmail(String userType, String code, String username, String password, String pin, long refId) {
		String methodName = "sendCreateNewUserEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setSettingType(Constant.SERVICE_NOTIFICATION_SETTING_TYPE_CREATE_USER);
			email.setSendingObject(Constant.SERVICE_NOTIFICATION_SENDING_OBJ);
			
			EmailListmsisdnDTO listmsisdn = new EmailListmsisdnDTO();
			switch(userType) {
				case Constant.MEMBER_MASTER_USER_PREFIX:
					listmsisdn.setMemberUser(Arrays.asList(username));
					break;
				case Constant.BROKER_USER_PREFIX:
					listmsisdn.setBroker(Arrays.asList(code));
					break;
				case Constant.COLLABORATOR_USER_PREFIX:
					listmsisdn.setCollaborator(Arrays.asList(code));
					break;
				case Constant.INVESTOR_USER_PREFIX:
					listmsisdn.setInvestorUser(Arrays.asList(username));
					break;
				default:
					listmsisdn.setAdmin(Arrays.asList(username));
					break;
			}
			
			email.setListmsisdn(listmsisdn);
			email.setSubject(FileUtility.CREATE_NEW_USER_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CREATE_NEW_USER_EMAIL_FILE), refId),
					username, password, pin);
			email.setBodyStr(emailBody);
			String emailJson = Utility.getGson().toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
    public static boolean setApprovalIDonRedis(RedisTemplate<String, String> template, String approvalId, long refId) {
    	long beginDate = System.currentTimeMillis();
    	long approvalIdTimeout = ConfigLoader.getMainConfig().getLong(Constant.REDIS_APPROVAL_ID_TIMEOUT);
		boolean result = template.opsForValue().setIfAbsent(approvalId, Constant.REDIS_APPROVAL_DEFAULT_VALUE, Duration.ofMillis(approvalIdTimeout));
		long endDate = System.currentTimeMillis();
		AMLogger.logMessage(className, "checkApprovalIDonRedis", refId, "Result=" + result + ", Duration=" + (endDate - beginDate));
		return result;
    }
    
    public static void deleteApprovalIDonRedis(RedisTemplate<String, String> template, String approvalId, long refId) {
    	AMLogger.logMessage(className, "deleteApprovalIDonRedis", refId, "REDIS_DELETE: key=" + approvalId);
		template.delete(approvalId);
    }
    
    public static String getSessionDate(long refId) {
		String methodName = "getSessionDate";
		String sessionDate = "";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			String[] res = serviceCon.sendGetRequest(serviceCon.getSessionDateServiceURL(),
					ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			if (res.length >= 2 && "200".equals(res[0])) {
				AdminResponseObj response = Utility.getGson().fromJson(res[1], AdminResponseObj.class);
				if (response != null && Constant.RESPONSE_OK.equalsIgnoreCase(response.getStatus())) {
					sessionDate = response.getData().getDate();
				}
			}
			return sessionDate;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
    public static String formatAmount(String input) {
		String formatted = "";
		if (isNotNull(input)) {
			for (int i = 0; i < input.length(); i++) {
				if (((i + 1) % 3 == input.length() % 3) && (i != input.length() - 1)) {
					formatted += input.charAt(i) + ".";
				} else {
					formatted += input.charAt(i);
				}
			}
		}
		return formatted;
	}
    
    public static String convertDoubleToString(double d) {
    	return new DecimalFormat("#.##").format(d);
    }
}
