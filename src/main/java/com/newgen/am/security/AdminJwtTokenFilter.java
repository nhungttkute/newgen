package com.newgen.am.security;

import com.newgen.am.common.AMLogger;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.newgen.am.exception.CustomException;
import org.springframework.web.filter.OncePerRequestFilter;

// We should use OncePerRequestFilter since we are doing a database call, there is no point in doing this more than once
public class AdminJwtTokenFilter extends OncePerRequestFilter {
    private String className = "AdminJwtTokenFilter";
    
    private AdminJwtTokenProvider admJwtTokenProvider;

    public AdminJwtTokenFilter(AdminJwtTokenProvider admJwtTokenProvider) {
        this.admJwtTokenProvider = admJwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String methodName = "doFilterInternal";
        long refId = System.currentTimeMillis();
        try {
            String token = admJwtTokenProvider.resolveToken(httpServletRequest);
            if (token != null && admJwtTokenProvider.validateToken(token)) {
                Authentication auth = admJwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException ex) {
            AMLogger.logError(className, methodName, refId, ex);
            //this is very important, since it guarantees the user is not authenticated at all
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(ex.getHttpStatus().value(), ex.getMessage());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
