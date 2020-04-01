package com.newgen.am.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class InvestorJwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

  private InvestorJwtTokenProvider invJwtTokenProvider;

  public InvestorJwtTokenFilterConfigurer(InvestorJwtTokenProvider invJwtTokenProvider) {
    this.invJwtTokenProvider = invJwtTokenProvider;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    InvestorJwtTokenFilter customFilter = new InvestorJwtTokenFilter(invJwtTokenProvider);
    http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
  }

}
