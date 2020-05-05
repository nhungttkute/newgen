package com.newgen.am.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.newgen.am.exception.CustomException;
import org.springframework.web.filter.OncePerRequestFilter;

// We should use OncePerRequestFilter since we are doing a database call, there is no point in doing this more than once
public class InvestorJwtTokenFilter extends OncePerRequestFilter {

  private InvestorJwtTokenProvider invJwtTokenProvider;

  public InvestorJwtTokenFilter(InvestorJwtTokenProvider invJwtTokenProvider) {
    this.invJwtTokenProvider = invJwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    String token = invJwtTokenProvider.resolveToken(httpServletRequest);
    try {
      if (token != null && invJwtTokenProvider.validateToken(token)) {
        Authentication auth = invJwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
      } else {
      	throw new CustomException("Invalid accessToken.", HttpStatus.UNAUTHORIZED);
      }
    } catch (CustomException ex) {
      //this is very important, since it guarantees the user is not authenticated at all
      SecurityContextHolder.clearContext();
      httpServletResponse.sendError(ex.getHttpStatus().value(), ex.getMessage());
      return;
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

}
