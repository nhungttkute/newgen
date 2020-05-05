/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.FileUtility;
import com.newgen.am.common.LocalServiceConnection;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.LoginAdminUserOutputDTO;
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Investor;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.mongodb.pojo.UserFunctionResult;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.security.AdminAuthenticationProvider;
import com.newgen.am.security.AdminJwtTokenProvider;
import com.newgen.am.security.AdminUsernamePasswordAuthenticationToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class LoginAdminUserService {

	private String className = "LoginAdminUserService";

	@Autowired
	private AdminAuthenticationProvider authenticationManager;

	@Autowired
	private AdminJwtTokenProvider admJwtTokenProvider;

	@Autowired
	private LoginAdminUserRepository loginAdmUserRepository;

	@Autowired
	private RedisTemplate template;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ActivityLogService activityLogService;

	private PasswordEncoder passwordEncoder;

	@Autowired
	public LoginAdminUserService(@Lazy PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public LoginAdminUserOutputDTO signin(HttpServletRequest request, String username, String password, long refId)
			throws CustomException {
		String methodName = "signin";
		// verify username/password
		Authentication auth = authenticationManager
				.authenticate(new AdminUsernamePasswordAuthenticationToken(username, password));
		String accessToken = admJwtTokenProvider.createToken(username, Utility.getAdminAuthorities());
		
		try {
			// get user
			LoginAdminUser user = loginAdmUserRepository.findByUsername(username);
			// delete old redis user info
			deleteOldRedisUserInfo(user.getAccessToken(), refId);

			// set new access token for user
			user.setAccessToken(accessToken);
			user.setTokenExpiration(admJwtTokenProvider.getTokenExpiration());
			user.setLogined(true);
			user.setLogonCounts(Utility.getInt(user.getLogonCounts()) + 1);
			user.setLogonTime(System.currentTimeMillis());
			LoginAdminUser loginUser = loginAdmUserRepository.save(user);

			// get investor user info
			UserInfoDTO userInfo = getAdminUserInfoDTO(user, refId);
			// put user info to redis
			Utility.setRedisUserInfo(template, loginUser.getAccessToken(), userInfo, refId);

			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_ADMIN_LOGIN,
					ActivityLogService.ACTIVITY_LOGIN_DESC, userInfo.getUsername(), "");

			LoginAdminUserOutputDTO loginUserInfoDto = modelMapper.map(userInfo, LoginAdminUserOutputDTO.class);
			return loginUserInfoDto;
		} catch (Exception ex) {
			AMLogger.logError(username, methodName, refId, ex);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private void deleteOldRedisUserInfo(String accessToken, long refId) {
		String key = Utility.genRedisKey(accessToken);
		AMLogger.logMessage(className, "deleteOldRedisUserInfo", refId, "REDIS_DELETE: key=" + key);
		template.delete(key);
	}

	public void logout(HttpServletRequest request, String userId, long refId) throws CustomException {
		String methodName = "logout";
		try {
			// get user
			LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, user.getAccessToken(), refId);
			// delete old redis user info
			deleteOldRedisUserInfo(user.getAccessToken(), refId);

			// delete access token
			user.setAccessToken(null);
			user.setLogined(false);
			loginAdmUserRepository.save(user);

			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_ADMIN_LOGOUT,
					ActivityLogService.ACTIVITY_LOGOUT_DESC, userInfo.getUsername(), "");
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean verifyPin(String userId, String pin, long refId) {
		String methodName = "verifyPin";
		try {
			LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
			if (user != null) {
				if (passwordEncoder.matches(pin, user.getPin()))
					return true;
				return false;
			} else {
				AMLogger.logMessage(className, methodName, refId, "Can't find user - id: " + userId);
				throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.OK);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void changePassword(HttpServletRequest request, String userId, String oldPassword, String newPassword,
			long refId) {
		String methodName = "changePassword";
		try {
			LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
			if (user != null) {
				if (passwordEncoder.matches(oldPassword, user.getPassword())) {
					user.setPassword(passwordEncoder.encode(newPassword));

					// set mustChangePassword=false (if user changed pass in the first logon time)
					user.setMustChangePassword(Boolean.FALSE);

					loginAdmUserRepository.save(user);
					
					// get redis user info
					UserInfoDTO userInfo = Utility.getRedisUserInfo(template, user.getAccessToken(), refId);
					// send activity log
					activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CHANGE_PASSWORD,
							ActivityLogService.ACTIVITY_CHANGE_PASSWORD_DESC, userInfo.getUsername(), "");
				} else {
					AMLogger.logMessage(newPassword, methodName, refId, ErrorMessage.INCORRECT_PASSWORD);
					throw new CustomException(ErrorMessage.INCORRECT_PASSWORD, HttpStatus.OK);
				}
			} else {
				AMLogger.logMessage(newPassword, methodName, refId, "Can't find user - id: " + userId);
				throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.OK);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public UserInfoDTO getAdminUserInfoDTO(LoginAdminUser user, long refId) {
		String methodName = "getAdminUserInfoDTO";
		UserInfoDTO userInfo = modelMapper.map(user, UserInfoDTO.class);
		List<String> allFunctions = new ArrayList<>();
		List<String> userFunctions = new ArrayList<>();
		List<String> roleFunctions = new ArrayList<>();
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", new Document().append("users.username", user.getUsername())),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users.username", user.getUsername())),
					new Document().append("$lookup",
							new Document().append("from", "system_roles").append("localField", "users.roles.name")
									.append("foreignField", "name").append("as", "roleObj")),
					new Document().append("$unwind", new Document().append("path", "$roleObj")),
					new Document().append("$project", new Document().append("deptId", "$_id")
							.append("deptCode", "$code").append("deptName", "$name").append("deptUserId", new Document().append("$toString", "$users._id"))
							.append("fullName", "$users.fullName").append("email", "$users.email")
							.append("phoneNumber", "$users.phoneNumber")
							.append("userFunctions",
									new Document().append("$concatArrays", Arrays.asList("$users.functions.code")))
							.append("roleFunctions",
									new Document().append("$concatArrays", Arrays.asList("$roleObj.functions.code")))));

			UserFunctionResult result = null;
			try (MongoCursor<Document> cur = collection.aggregate(pipeline).iterator()) {

				while (cur.hasNext()) {
					result = mongoTemplate.getConverter().read(UserFunctionResult.class, cur.next());
					if (result.getUserFunctions() != null)
						userFunctions = result.getUserFunctions();
					if (result.getRoleFunctions() != null)
						roleFunctions.addAll(result.getRoleFunctions());
				}
			}

			allFunctions.addAll(userFunctions);
			allFunctions.addAll(roleFunctions);
			if (result != null) {
				userInfo.setDeptId(result.getDeptId());
				userInfo.setDeptCode(result.getDeptCode());
				userInfo.setDeptName(result.getDeptName());
				userInfo.setDeptUserId(result.getDeptUserId());
				userInfo.setFullName(result.getFullName());
				userInfo.setEmail(result.getEmail());
				userInfo.setPhoneNumber(result.getPhoneNumber());
			}
			userInfo.setFunctions(allFunctions);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userInfo;
	}

	public List<String> getFunctionsByUsername(String username) {
		String methodName = "getFunctionsByUsername";
		List<String> allFunctions = new ArrayList<>();
		List<String> userFunctions = new ArrayList<>();
		List<String> roleFunctions = new ArrayList<>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", new Document().append("users.username", username)),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users.username", username)),
					new Document().append("$lookup",
							new Document().append("from", "system_roles").append("localField", "users.roles.name")
									.append("foreignField", "name").append("as", "roleObj")),
					new Document().append("$unwind", new Document().append("path", "$roleObj")),
					new Document().append("$project",
							new Document()
									.append("userFunctions",
											new Document().append("$concatArrays",
													Arrays.asList("$users.functions.code")))
									.append("roleFunctions", new Document().append("$concatArrays",
											Arrays.asList("$roleObj.functions.code")))));

			UserFunctionResult result = null;
			try (MongoCursor<Document> cur = collection.aggregate(pipeline).iterator()) {

				while (cur.hasNext()) {
					result = mongoTemplate.getConverter().read(UserFunctionResult.class, cur.next());
					if (result.getUserFunctions() != null)
						userFunctions = result.getUserFunctions();
					if (result.getRoleFunctions() != null)
						roleFunctions.addAll(result.getRoleFunctions());
				}
			}

			allFunctions.addAll(userFunctions);
			allFunctions.addAll(roleFunctions);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, 999999999, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allFunctions;
	}

	public LoginAdminUser search(String userId, long refId) throws CustomException {
		String methodName = "search";
		try {
			LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
			return user;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public LoginAdminUser save(LoginAdminUser user, long refId) throws CustomException {
		String methodName = "save";
		try {
			return loginAdmUserRepository.save(user);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void resetAdminUserPassword(HttpServletRequest request, LoginUserDataInputDTO user, long refId) {
		String methodName = "resetAdminUserPassword";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("login_admin_users");

			BasicDBObject query = new BasicDBObject();
			query.put("username", user.getUsername());

			String newPassword = Utility.generateRandomPassword();
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("password", passwordEncoder.encode(newPassword));
			newDocument.put("mustChangePassword", true);
			newDocument.put("lastModifiedUser", Utility.getCurrentUsername());
			newDocument.put("lastModifiedDate", System.currentTimeMillis());

			BasicDBObject update = new BasicDBObject();
			update.put("$set", newDocument);

			collection.updateOne(query, update);

			// send email
			sendChangePasswordEmail(user.getEmail(), user.getUsername(), newPassword, refId);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_RESET_PASSWORD,
					ActivityLogService.ACTIVITY_RESET_PASSWORD_DESC, userInfo.getUsername(), "");
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void resetAdminUserPin(HttpServletRequest request, LoginUserDataInputDTO user, long refId) {
		String methodName = "resetAdminUserPin";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("login_admin_users");

			BasicDBObject query = new BasicDBObject();
			query.put("username", user.getUsername());

			String newPin = Utility.generateRandomPin();
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("pin", passwordEncoder.encode(newPin));
			newDocument.put("lastModifiedUser", Utility.getCurrentUsername());
			newDocument.put("lastModifiedDate", System.currentTimeMillis());

			BasicDBObject update = new BasicDBObject();
			update.put("$set", newDocument);

			collection.updateOne(query, update);

			// send email
			sendChangePINEmail(user.getEmail(), user.getUsername(), newPin, refId);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_RESET_PIN,
					ActivityLogService.ACTIVITY_RESET_PIN_DESC, userInfo.getUsername(), "");
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendChangePasswordEmail(String toEmail, String username, String password, long refId) {
		String methodName = "sendChangePasswordEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setTo(toEmail);
			email.setSubject(FileUtility.CHANGE_PASSWORD_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CHANGE_PASSWORD_EMAIL_FILE), refId),
					username, password);
			email.setBodyStr(emailBody);
			String emailJson = new Gson().toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendChangePINEmail(String toEmail, String username, String pin, long refId) {
		String methodName = "sendChangePINEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setTo(toEmail);
			email.setSubject(FileUtility.CHANGE_PIN_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CHANGE_PIN_EMAIL_FILE), refId),
					username, pin);
			email.setBodyStr(emailBody);
			String emailJson = new Gson().toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
