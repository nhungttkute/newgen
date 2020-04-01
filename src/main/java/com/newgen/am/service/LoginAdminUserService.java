/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.security.AdminAuthenticationProvider;
import com.newgen.am.security.AdminJwtTokenProvider;
import com.newgen.am.security.AdminUsernamePasswordAuthenticationToken;
import java.util.Arrays;
import java.util.Date;
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
            user.setLogonTime(new Date());
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

    public boolean logout(Long userId, long refId) throws CustomException {
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

    public boolean verifyPin(Long userId, String pin, long refId) {
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

    public boolean changePassword(Long userId, String oldPassword, String newPassword, long refId) {
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

    public List<String> getFunctionsByUserId(Long userId) {
        MongoDatabase database = MongoDBConnection.getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("departments");

        List<? extends Bson> pipeline = Arrays.asList(
                new Document()
                .append("$match", new Document()
                        .append("users._id", 4.0)
                ),
                new Document()
                .append("$unwind", new Document()
                        .append("path", "$users")
                ),
                new Document()
                .append("$match", new Document()
                        .append("users._id", 4.0)
                ),
                new Document()
                .append("$lookup", new Document()
                        .append("from", "system_roles")
                        .append("localField", "users.roles.name")
                        .append("foreignField", "name")
                        .append("as", "roleObj")
                ),
                new Document()
                .append("$project", new Document()
                        .append("_id", 0.0)
                        .append("functions", new Document()
                                .append("$concatArrays", Arrays.asList(
                                                "$users.functions.code",
                                                new Document()
                                                .append("$arrayElemAt", Arrays.asList(
                                                                "$roleObj.functions.code",
                                                                0.0
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        Document result = collection.aggregate(pipeline).first();
        UserInfoDTO userInfo = new Gson().fromJson(result.toJson(), UserInfoDTO.class);
        return userInfo.getFunctions();
    }
}
