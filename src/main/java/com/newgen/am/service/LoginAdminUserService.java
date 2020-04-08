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
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.mongodb.pojo.UserFunctionResult;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.security.AdminAuthenticationProvider;
import com.newgen.am.security.AdminJwtTokenProvider;
import com.newgen.am.security.AdminUsernamePasswordAuthenticationToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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

    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginAdminUserService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LoginAdminUser signin(String username, String password, long refId) throws CustomException {
        String methodName = "signin";
        try {
            // verify username/password
            authenticationManager.authenticate(new AdminUsernamePasswordAuthenticationToken(username, password));
            String accessToken = admJwtTokenProvider.createToken(username, Utility.getAdminAuthorities());

            // get reids secret key
//            String secretKey = ConfigLoader.getMainConfig().getString(Constant.REDIS_KEY_SECRET_KEY);
            //get user
            LoginAdminUser user = loginAdmUserRepository.findByUsername(username);
//            //delete old redis user info
//            deleteOldRedisUserInfo(user, refId);

            //set new access token for user
            user.setAccessToken(accessToken);
            user.setTokenExpiration(admJwtTokenProvider.getTokenExpiration());
            user.setLogined(true);
            user.setLogonCounts(Utility.getInt(user.getLogonCounts()) + 1);
            user.setLogonTime(System.currentTimeMillis());
            LoginAdminUser loginUser = loginAdmUserRepository.save(user);

//            //put user info to redis
//            String investorCode = setRedisUserInfo(loginUser, refId);
//
//            //get user info from redis
//            getRedisUserInfo(loginUser, refId);
//            //send activity log
//            Utility.logActivity(ActivityLogConstant.ACTIVITY_ORG_TYPE_TKGD, investorCode, loginUser.getId().toString(), loginUser.getUsername(), ActivityLogConstant.ACTIVITY_TP_LOGIN, accessToken, "");
            return loginUser;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public boolean logout(long userId, long refId) throws CustomException {
        String methodName = "logout";
        try {
            //get user
            LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
//            // delete old redis user info
//            deleteOldRedisUserInfo(user, refId);

            //delete access token
            user.setAccessToken(null);
            user.setLogined(false);
            loginAdmUserRepository.save(user);
            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public boolean verifyPin(long userId, String pin, long refId) {
        String methodName = "verifyPin";
        try {
            LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
            if (user.getPin().equalsIgnoreCase(pin)) {
                return true;
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
        }
        return false;
    }

    public boolean changePassword(long userId, String oldPassword, String newPassword, long refId) {
        String methodName = "changePassword";
        try {
            LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
            if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));

                // set mustChangePassword=false (if user changed pass in the first logon time)
                user.setMustChangePassword(Boolean.FALSE);

                LoginAdminUser newUser = loginAdmUserRepository.save(user);
                if (newUser != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
        }
        return false;
    }

    public List<String> getFunctionsByUserId(long userId) {
        String methodName = "getFunctionsByUserId";
        List<String> allFunctions = new ArrayList<>();
        List<String> userFunctions = new ArrayList<>();
        List<String> roleFunctions = new ArrayList<>();
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("departments");

            List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("users._id", userId)
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$users")
                            ), 
                    new Document()
                            .append("$match", new Document()
                                    .append("users._id", userId)
                            ), 
                    new Document()
                            .append("$lookup", new Document()
                                    .append("from", "system_roles")
                                    .append("localField", "users.roles.name")
                                    .append("foreignField", "name")
                                    .append("as", "roleObj")
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$roleObj")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", 0.0)
                                    .append("userFunctions", new Document()
                                            .append("$concatArrays", Arrays.asList(
                                                    "$users.functions.code"
                                                )
                                            )
                                    )
                                    .append("roleFunctions", new Document()
                                            .append("$concatArrays", Arrays.asList(
                                                    "$roleObj.functions.code"
                                                )
                                            )
                                    )
                            )
            );
            
            try (MongoCursor<Document> cur = collection.aggregate(pipeline).iterator()) {

                while (cur.hasNext()) {
                    Document doc = cur.next();
                    UserFunctionResult result = new Gson().fromJson(doc.toJson(Utility.getJsonWriterSettings()), UserFunctionResult.class);
                    if (result.getUserFunctions() != null) userFunctions = result.getUserFunctions();
                    if (result.getRoleFunctions() != null) roleFunctions.addAll(result.getRoleFunctions());
                }
            }
            
            allFunctions.addAll(userFunctions);
            allFunctions.addAll(roleFunctions);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, userId, e);
        }
        return allFunctions;
    }
    
    public LoginAdminUser search(long userId, long refId) throws CustomException {
        String methodName = "search";
        try {
            LoginAdminUser user = loginAdmUserRepository.findById(userId).get();
            return user;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
        }
    }
    
    public LoginAdminUser save(LoginAdminUser user, long refId) throws CustomException {
        String methodName = "save";
        try {
            return loginAdmUserRepository.save(user);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot save the user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public boolean resetAdminUserPassword(LoginUserDataInputDTO user, long refId) {
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
            
            BasicDBObject update = new BasicDBObject();
            update.put("$set", newDocument);
            
            collection.updateOne(query, update);
            
            //send email
            sendChangePasswordEmail(user.getEmail(), user.getUsername(), newPassword, refId);
            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot view this user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    private void sendChangePasswordEmail(String toEmail, String username, String password, long refId) {
        String methodName = "sendChangePasswordEmail";
        try {
            LocalServiceConnection serviceCon = new LocalServiceConnection();
            EmailDTO email = new EmailDTO();
            email.setTo(toEmail);
            email.setSubject(FileUtility.CHANGE_PASSWORD_EMAIL_SUBJECT);

            FileUtility fileUtility = new FileUtility();
            String emailBody = String.format(fileUtility.loadFileContent(ConfigLoader.getMainConfig().getString(FileUtility.CHANGE_PASSWORD_EMAIL_FILE), refId), username, password);
            email.setBodyStr(emailBody);
            String emailJson = new Gson().toJson(email);
            AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
            serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
    }
}
