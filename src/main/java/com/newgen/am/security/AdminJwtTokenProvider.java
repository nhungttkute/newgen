package com.newgen.am.security;

import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.newgen.am.exception.CustomException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class AdminJwtTokenProvider {

    private String secretKey;

    private long validityInMilliseconds;

    private long tokenExpiration;

    @Autowired
    private HybridUserDetailsService userService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(ConfigLoader.getMainConfig().getString(Constant.ADMIN_JWT_SECRET).getBytes());
        validityInMilliseconds = ConfigLoader.getMainConfig().getLong(Constant.ADMIN_JWT_EXPIRATION, 3600000);
    }

    public String createToken(String username, List<SimpleGrantedAuthority> roles) {

        Claims claims = Jwts.claims().setSubject(username);
//    claims.put("auth", roles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).filter(Objects::nonNull).collect(Collectors.toList()));
        claims.put("auth", roles.stream().filter(Objects::nonNull).collect(Collectors.toList()));

        Date now = new Date();
        tokenExpiration = now.getTime() + validityInMilliseconds;

        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(new Date(tokenExpiration))//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.checkAccessTokenByAdminUser(getUsername(token), token);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    
    public Date getExpiration(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public long getTokenExpiration() {
        return tokenExpiration;
    }

}
