package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.UserInfoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.security.InvestorAuthenticationProvider;
//import com.newgen.am.security.InvestorJwtTokenProvider;
import com.newgen.am.security.InvestorJwtTokenProvider;
import com.newgen.am.security.InvestorUsernamePasswordAuthenticationToken;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class LoginInvestorUserService {

    private String className = "LoginInvestorUserService";

    @Autowired
    private LoginInvestorUserRepository loginInvUserRepository;

    @Autowired
    private InvestorJwtTokenProvider invJwtTokenProvider;

    @Autowired
    private InvestorAuthenticationProvider authenticationManager;

    @Autowired
    private RedisTemplate template;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginInvestorUserService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LoginInvestorUser signin(String username, String password, long refId) throws CustomException {
        String methodName = "signin";
        try {
            // verify username/password
            authenticationManager.authenticate(new InvestorUsernamePasswordAuthenticationToken(username, password));
            String accessToken = invJwtTokenProvider.createToken(username, Utility.getInvestorAuthorities());

            // get reids secret key
            String secretKey = ConfigLoader.getMainConfig().getString(Constant.REDIS_KEY_SECRET_KEY);
            //get user
            LoginInvestorUser user = loginInvUserRepository.findByUsername(username);
            //delete old redis user info
            deleteOldRedisUserInfo(user, refId);

            //set new access token for user
            user.setAccessToken(accessToken);
            user.setTokenExpiration(invJwtTokenProvider.getTokenExpiration());
            user.setLogined(true);
            user.setLogonCounts(Utility.getInt(user.getLogonCounts()) + 1);
            user.setLogonTime(new Date());
            LoginInvestorUser loginUser = loginInvUserRepository.save(user);

            //put user info to redis
            String investorCode = setRedisUserInfo(loginUser, refId);

            //get user info from redis
            getRedisUserInfo(loginUser, refId);

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
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            // delete old redis user info
            deleteOldRedisUserInfo(user, refId);

            //delete access token
            user.setAccessToken(null);
            user.setLogined(false);
            loginInvUserRepository.save(user);
            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public LoginInvestorUser save(LoginInvestorUser user, long refId) throws CustomException {
        String methodName = "save";
        try {
            return loginInvUserRepository.save(user);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot save the user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public List<LoginInvestorUser> list(long refId) throws CustomException {
        String methodName = "list";
        try {
            return loginInvUserRepository.findAll();
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot list users", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public LoginInvestorUser search(long userId, long refId) throws CustomException {
        String methodName = "search";
        try {
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            return user;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
        }
    }

    public String refresh(String username, long refId) throws CustomException {
        String methodName = "refresh";
        try {
            return invJwtTokenProvider.createToken(username, Utility.getInvestorAuthorities());
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot refresh token", HttpStatus.NOT_FOUND);
        }
    }

    public boolean verifyPin(long userId, String pin, long refId) {
        String methodName = "verifyPin";
        try {
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
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
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));

                // set mustChangePassword=false (if user changed pass in the first logon time)
                user.setMustChangePassword(Boolean.FALSE);

                LoginInvestorUser newUser = loginInvUserRepository.save(user);
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

    private void deleteOldRedisUserInfo(LoginInvestorUser user, long refId) {
        String key = Utility.genRedisKey(user.getAccessToken());
        AMLogger.logMessage(className, "deleteOldRedisUserInfo", refId, "REDIS_DELETE: key=" + key);
        template.delete(key);
    }

    private String setRedisUserInfo(LoginInvestorUser user, long refId) {
        UserInfoDTO userInfo = getUserInfoDTO(user, refId);
        String investorCode = userInfo.getInvestorCode();
        String key = Utility.genRedisKey(user.getAccessToken());
        String value = new Gson().toJson(userInfo);
        AMLogger.logMessage(className, "setRedisUserInfo", refId, "REDIS_SET: key=" + key + ", value=" + value);
        template.opsForValue().set(key, value);
        return investorCode;
    }

    private void getRedisUserInfo(LoginInvestorUser user, long refId) {
        String key = Utility.genRedisKey(user.getAccessToken());
        String value = (String) template.opsForValue().get(key);
        AMLogger.logMessage(className, "getRedisUserInfo", refId, "REDIS_GET: key=" + key + ", value=" + value);
    }

    private UserInfoDTO getUserInfoDTO(LoginInvestorUser user, long refId) {
        UserInfoDTO userInfoDto = new UserInfoDTO();
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("investors");

            List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                    .append("$match", new Document()
                            .append("_id", 15.0)
                    ),
                    new Document()
                    .append("$lookup", new Document()
                            .append("from", "system_roles")
                            .append("localField", "role.name")
                            .append("foreignField", "name")
                            .append("as", "roleObj")
                    ),
                    new Document()
                    .append("$project", new Document()
                            .append("memberCode", 1.0)
                            .append("memberName", 1.0)
                            .append("brokerCode", 1.0)
                            .append("brokerName", 1.0)
                            .append("investorCode", 1.0)
                            .append("investorName", 1.0)
                            .append("collaboratorCode", 1.0)
                            .append("collaboratorName", 1.0)
                            .append("account", 1.0)
                            .append("orderLimit", 1.0)
                            .append("commodities", 1.0)
                            .append("marginRatioAlert", 1.0)
                            .append("marginMultiplier", 1.0)
                            .append("otherFee", 1.0)
                            .append("functions", new Document()
                                    .append("$arrayElemAt", Arrays.asList(
                                                    "$roleObj.functions.code",
                                                    0.0
                                            )
                                    )
                            )
                    )
            );

            Document result = collection.aggregate(pipeline).first();
            userInfoDto = new Gson().fromJson(result.toJson(), UserInfoDTO.class);
            if (userInfoDto != null) {
                userInfoDto.setId(user.getId());
                userInfoDto.setUsername(user.getUsername());
                userInfoDto.setPin(user.getPin());
                userInfoDto.setStatus(user.getStatus());
                userInfoDto.setTokenExpiration(user.getTokenExpiration());
                userInfoDto.setInvestorUserId(user.getInvestorUserId());
            }
        } catch (Exception e) {
            AMLogger.logError(className, "getUserInfoDTO", refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return userInfoDto;
    }
}
