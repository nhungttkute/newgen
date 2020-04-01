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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * @author nhungtt
 */
@Configuration
@EnableWebSecurity
public class MultipleLoginSecurityConfig {

    @Autowired
    private static InvestorJwtTokenProvider invJwtTokenProvider;

    @Autowired
    private static InvestorAuthenticationProvider invAuthenticationProvider;

    @Autowired
    private static AdminJwtTokenProvider admJwtTokenProvider;

    @Autowired
    private static AdminAuthenticationProvider admAuthenticationProvider;

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
    public static class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

        public AdminWebSecurityConfig() {
            super();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Disable CSRF (cross site request forgery)
            http.csrf().disable();
            // No session will be created or used by spring security
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            // Apply JWT
            http.apply(new AdminJwtTokenFilterConfigurer(admJwtTokenProvider));
            
            http.antMatcher("/admin*")
                    .authorizeRequests()
                    .antMatchers("/admin/users/login").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
            
            // If a user try to access a resource without having enough permissions
            http.exceptionHandling().accessDeniedPage("/login");
        }

    }

    @Configuration
    @Order(2)
    public static class InvestorWebSecurityConfig extends WebSecurityConfigurerAdapter {

        public InvestorWebSecurityConfig() {
            super();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Disable CSRF (cross site request forgery)
            http.csrf().disable();
           // No session will be created or used by spring security
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            // Apply JWT
            http.apply(new InvestorJwtTokenFilterConfigurer(invJwtTokenProvider));
            
            // Entry points
            http.antMatcher("/users*")
                    .authorizeRequests()
                    .antMatchers("/users/login").permitAll()
                    .antMatchers("/users/**").hasRole("INVESTOR")
                    .anyRequest().authenticated();

            // If a user try to access a resource without having enough permissions
            http.exceptionHandling().accessDeniedPage("/login");

            
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
