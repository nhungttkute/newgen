package com.newgen.am.service;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ActivityLogConstant;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.UserInfoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Investor;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.security.JwtTokenProvider;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class LoginInvestorUserService {

    private String className = "LoginInvestorUserService";
    
    @Autowired
    private LoginInvestorUserRepository loginInvUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate template;

    public LoginInvestorUser signin(String username, String password, long refId) throws CustomException {
        String methodName = "signin";
        try {
            // verify username/password
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String accessToken = jwtTokenProvider.createToken(username, Utility.getAuthority());
            
            // get reids secret key
            String secretKey = ConfigLoader.getMainConfig().getString(Constant.REDIS_KEY_SECRET_KEY);
            //get user
            LoginInvestorUser user = loginInvUserRepository.findByUsername(username);
            //delete old redis user info
            deleteOldRedisUserInfo(user, refId);
            
            //set new access token for user
            user.setAccessToken(accessToken);
            user.setTokenExpiration(jwtTokenProvider.getTokenExpiration());
            user.setLogined(true);
            user.setLogonCounts(user.getLogonCounts() + 1);
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

    public boolean logout(Long userId, long refId) throws CustomException {
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

    public LoginInvestorUser signup(LoginInvestorUser user, long refId) throws CustomException {
        String methodName = "signup";
        if (!loginInvUserRepository.existsByUsername(user.getUsername())) {
            try {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                LoginInvestorUser userObj = loginInvUserRepository.save(user);
                return userObj;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot create new user", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void delete(Long userId, long refId) throws CustomException {
        String methodName = "delete";
        try {
            loginInvUserRepository.deleteById(userId);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot delete the user", HttpStatus.UNPROCESSABLE_ENTITY);
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

    public LoginInvestorUser search(Long userId, long refId) throws CustomException {
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
            return jwtTokenProvider.createToken(username, Utility.getAuthority());
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot refresh token", HttpStatus.NOT_FOUND);
        }
    }

    public boolean verifyPin(Long userId, String pin, long refId) {
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

    public boolean changePassword(Long userId, String oldPassword, String newPassword, long refId) {
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
        AMLogger.logMessage(className, "putRedisUserInfo", refId, "REDIS_DELETE: key=" + key);
        template.delete(key);
    }
    
    private String setRedisUserInfo(LoginInvestorUser user, long refId) {
        UserInfoDTO userInfo = getUserInfoDTO(user);
        String investorCode = userInfo.getInvestorCode();
        String key = Utility.genRedisKey(user.getAccessToken());
        String value = new Gson().toJson(userInfo);
        AMLogger.logMessage(className, "putRedisUserInfo", refId, "REDIS_SET: key=" + key + ", value=" + value);
        template.opsForValue().set(key, value);
        return investorCode;
    }
    
    private void getRedisUserInfo(LoginInvestorUser user, long refId) {
        String key = Utility.genRedisKey(user.getAccessToken());
        String value = (String) template.opsForValue().get(key);
        AMLogger.logMessage(className, "putRedisUserInfo", refId, "REDIS_GET: key=" + key + ", value=" + value);
    }

    private UserInfoDTO getUserInfoDTO(LoginInvestorUser user) {
        // crack investors for info
        MatchOperation matchStage = Aggregation.match(new Criteria("_id").is(user.getInvestorId()));
        LookupOperation lookupStage = Aggregation.lookup("system_roles", "role.name", "name", "roleObj");
        ProjectionOperation projectStage = Aggregation.project("memberCode", "brokerCode", "collaboratorCode", "account", "orderLimit", "commodities", "marginRatioAlert", "marginMultiplier", "otherFee")
                .and("code").as("investorCode")
                .and(ArrayOperators.ArrayElemAt.arrayOf("roleObj.functions").elementAt(0)).as("functions");
        Aggregation aggregation = Aggregation.newAggregation(matchStage, lookupStage, projectStage);
        AggregationResults<UserInfoDTO> output
                = mongoTemplate.aggregate(aggregation, Investor.class, UserInfoDTO.class);
        UserInfoDTO userInfoDto = output.getUniqueMappedResult();

        // set other info from loginInvestorUser
        if (userInfoDto != null) {
            userInfoDto.setId(user.getId());
            userInfoDto.setUsername(user.getUsername());
            userInfoDto.setPin(user.getPin());
            userInfoDto.setStatus(user.getStatus());
            userInfoDto.setTokenExpiration(user.getTokenExpiration());
            userInfoDto.setInvestorUserId(user.getInvestorUserId());
        }
        return userInfoDto;
    }
}
