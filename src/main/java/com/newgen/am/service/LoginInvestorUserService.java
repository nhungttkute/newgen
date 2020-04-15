package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.LoginInvestorUserOutputDTO;
import com.newgen.am.dto.UserInfoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.security.InvestorAuthenticationProvider;
import com.newgen.am.security.InvestorJwtTokenProvider;
import com.newgen.am.security.InvestorUsernamePasswordAuthenticationToken;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ActivityLogService activityLogService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginInvestorUserService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public LoginInvestorUserOutputDTO signin(HttpServletRequest request, String username, String password, long refId) throws CustomException {
        String methodName = "signin";
        try {
            // verify username/password, status
            authenticationManager.authenticate(new InvestorUsernamePasswordAuthenticationToken(username, password));
            String accessToken = invJwtTokenProvider.createToken(username, Utility.getInvestorAuthorities());

            //get user
            LoginInvestorUser user = loginInvUserRepository.findByUsername(username);
            //delete old redis user info
            deleteOldRedisUserInfo(user.getAccessToken(), refId);

            //set new access token for user
            user.setAccessToken(accessToken);
            user.setTokenExpiration(invJwtTokenProvider.getTokenExpiration());
            user.setLogined(true);
            user.setLogonCounts(Utility.getInt(user.getLogonCounts()) + 1);
            user.setLogonTime(new Date());
            LoginInvestorUser loginUser = loginInvUserRepository.save(user);

            //get investor user info
            UserInfoDTO userInfo = getInvestorUserInfoDTO(user, refId);
            //put user info to redis
            Utility.setRedisUserInfo(template, loginUser.getAccessToken(), userInfo, refId);

            //send activity log
            activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_TP_LOGIN, ActivityLogService.ACTIVITY_LOGIN_DESC, userInfo.getUsername(), 0);
            LoginInvestorUserOutputDTO loginUserInfoDto = modelMapper.map(userInfo, LoginInvestorUserOutputDTO.class);
            return loginUserInfoDto;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public boolean logout(HttpServletRequest request, long userId, long refId) throws CustomException {
        String methodName = "logout";
        try {
            //get user
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            // get redis user info
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, user.getAccessToken(), refId);
            // delete old redis user info
            deleteOldRedisUserInfo(user.getAccessToken(), refId);

            //delete access token
            user.setAccessToken(null);
            user.setLogined(false);
            loginInvUserRepository.save(user);

            //send activity log
            activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_TP_LOGOUT, ActivityLogService.ACTIVITY_LOGOUT_DESC, userInfo.getUsername(), 0);
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
            if (user != null && passwordEncoder.matches(pin, user.getPin())) {
                return true;
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
        }
        return false;
    }

    public boolean changePassword(HttpServletRequest request, long userId, String oldPassword, String newPassword, long refId) {
        String methodName = "changePassword";
        try {
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));

                // set mustChangePassword=false (if user changed pass in the first logon time)
                user.setMustChangePassword(Boolean.FALSE);

                LoginInvestorUser newUser = loginInvUserRepository.save(user);
                if (newUser != null) {
                    // get redis user info
                    UserInfoDTO userInfo = Utility.getRedisUserInfo(template, user.getAccessToken(), refId);
                    //send activity log
                    activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CHANGE_PASSWORD, ActivityLogService.ACTIVITY_CHANGE_PASSWORD_DESC, userInfo.getUsername(), 0);

                    return true;
                }
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
        }
        return false;
    }

    private void deleteOldRedisUserInfo(String accessToken, long refId) {
        String key = Utility.genRedisKey(accessToken);
        AMLogger.logMessage(className, "deleteOldRedisUserInfo", refId, "REDIS_DELETE: key=" + key);
        template.delete(key);
    }

    private UserInfoDTO getInvestorUserInfoDTO(LoginInvestorUser user, long refId) {
        UserInfoDTO userInfoDto = new UserInfoDTO();
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("investors");

            List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                    .append("$match", new Document()
                            .append("users.username", user.getUsername())
                    ),
                    new Document()
                    .append("$unwind", new Document()
                            .append("path", "$users")
                    ),
                    new Document()
                    .append("$match", new Document()
                            .append("users.username", user.getUsername())
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
                            .append("investorId", "$_id")
                            .append("investorUserId", "$users._id")
                            .append("fullName", "$users.fullName")
                            .append("email", "$users.email")
                            .append("phoneNumber", "$users.phoneNumber")
                            .append("memberId", 1.0)
                            .append("brokerId", 1.0)
                            .append("collaboratorId", 1.0)
                            .append("memberCode", 1.0)
                            .append("memberName", 1.0)
                            .append("brokerCode", 1.0)
                            .append("brokerName", 1.0)
                            .append("collaboratorCode", 1.0)
                            .append("collaboratorName", 1.0)
                            .append("investorCode", 1.0)
                            .append("investorName", 1.0)
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
            System.out.println("Document: " + result.toJson(Utility.getJsonWriterSettings()));
            userInfoDto = new Gson().fromJson(result.toJson(Utility.getJsonWriterSettings()), UserInfoDTO.class);
            System.out.println("userInfoDto: " + new Gson().toJson(userInfoDto));
            if (userInfoDto != null) {
                userInfoDto.setId(user.getId());
                userInfoDto.setUsername(user.getUsername());
                userInfoDto.setStatus(user.getStatus());
                userInfoDto.setAccessToken(user.getAccessToken());
                userInfoDto.setTokenExpiration(user.getTokenExpiration());
                userInfoDto.setLogined(user.getLogined());
                userInfoDto.setMustChangePassword(user.getMustChangePassword());
                userInfoDto.setWatchLists(user.getWatchlists());
                userInfoDto.setLayout(user.getLayout());
                userInfoDto.setTheme(user.getTheme());
                userInfoDto.setLanguage(user.getLanguage());
                userInfoDto.setFontSize(user.getFontSize());
            }
        } catch (Exception e) {
            AMLogger.logError(className, "getUserInfoDTO", refId, e);
            throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return userInfoDto;
    }

    private void copyLoginUserProperties(UserInfoDTO userInfoDto, LoginInvestorUser user) {

    }
}
