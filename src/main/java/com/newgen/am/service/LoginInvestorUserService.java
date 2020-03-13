package com.newgen.am.service;

import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.security.JwtTokenProvider;
import java.util.List;

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

    public LoginInvestorUser signin(String username, String password, long refId) throws CustomException {
        String methodName = "signin";
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String accessToken = jwtTokenProvider.createToken(username, Utility.getAuthority());

            LoginInvestorUser user = loginInvUserRepository.findByUsername(username);
            user.setAccessToken(accessToken);
            user.setTokenExpiration(jwtTokenProvider.getTokenExpiration());
            user.setLogined(true);
            LoginInvestorUser loginUser = loginInvUserRepository.save(user);
            return loginUser;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public LoginInvestorUser signup(LoginInvestorUser user, long refId) throws CustomException {
        String methodName = "signin";
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
        String methodName = "save";
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
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
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
        String methodName = "search";
        try {
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            if (user.getPin().equalsIgnoreCase(pin)) return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return false;
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword, long refId) {
        String methodName = "changePassword";
        try {
            LoginInvestorUser user = loginInvUserRepository.findById(userId).get();
            if (user!= null && passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                LoginInvestorUser newUser = loginInvUserRepository.save(user);
                if (newUser != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return false;
    }
}
