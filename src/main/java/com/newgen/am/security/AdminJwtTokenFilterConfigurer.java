package com.newgen.am.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class AdminJwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private AdminJwtTokenProvider adminJwtTokenProvider;

  public AdminJwtTokenFilterConfigurer(AdminJwtTokenProvider adminJwtTokenProvider) {
    this.adminJwtTokenProvider = adminJwtTokenProvider;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    AdminJwtTokenFilter customFilter = new AdminJwtTokenFilter(adminJwtTokenProvider);
    http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
  }

}
