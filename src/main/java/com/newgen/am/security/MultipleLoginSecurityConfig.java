/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author nhungtt
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultipleLoginSecurityConfig {

    @Autowired
    private InvestorJwtTokenProvider invJwtTokenProvider;

    @Autowired
    private InvestorAuthenticationProvider invAuthenticationProvider;

    @Autowired
    private AdminJwtTokenProvider admJwtTokenProvider;

    @Autowired
    private AdminAuthenticationProvider admAuthenticationProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(admAuthenticationProvider).authenticationProvider(invAuthenticationProvider);
    }

    @Configuration
    @Order(1)
    public class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

        public AdminWebSecurityConfig() {
            super();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and()
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
                .antMatcher("/admin/**")
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .addFilterBefore(new AdminJwtTokenFilter(admJwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

            http.csrf().disable();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            // TokenAuthenticationFilter will ignore the below paths
            web.ignoring().antMatchers(
                    HttpMethod.POST,
                    "/admin/users/login"
            ).antMatchers(
                    HttpMethod.POST,
                    "/admin/investorInfo"
            ).antMatchers(
                    HttpMethod.POST,
                    "/admin/investorInfo/cqgAccount"
            ).antMatchers(
                    HttpMethod.POST,
                    "/admin/investors/commodityFee"
            ).antMatchers(
                    HttpMethod.GET,
                    "/admin/info"
            );
        }

    }

    @Configuration
    @Order(2)
    public class InvestorWebSecurityConfig extends WebSecurityConfigurerAdapter {

        public InvestorWebSecurityConfig() {
            super();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors().and()
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
                .antMatcher("/users/**")
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new InvestorJwtTokenFilter(invJwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

            http.csrf().disable();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            // TokenAuthenticationFilter will ignore the below paths
            web.ignoring().antMatchers(
                    HttpMethod.POST,
                    "/users/login"
            );
        }
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Access-Control-Allow-Headers", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
