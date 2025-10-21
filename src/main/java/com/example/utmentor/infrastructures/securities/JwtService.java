package com.example.utmentor.infrastructures.securities;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        
        return Jwts.builder()
                .subject(subject)
                .issuer(issuer)
                .claims(claims)
                .issuedAt(Date.from(now))        // Automatically adds "iat" claim
                .expiration(Date.from(exp))      // Automatically adds "exp" claim
                .signWith(secretKey, io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Claims parseAndValidate(String token) throws JwtException {
        // Sẽ ném ExpiredJwtException nếu exp quá hạn, JwtException cho các lỗi khác
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String extractSubject(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        Object raw = claims.get("roles"); // ví dụ ["ADMIN","TUTOR"] hoặc "ADMIN,TUTOR"
        List<String> roles;
        if (raw instanceof List<?> list) {
            roles = list.stream().map(String::valueOf).toList();
        } else if (raw instanceof String s) {
            roles = Arrays.stream(s.split(",")).map(String::trim).filter(x->!x.isEmpty()).toList();
        } else {
            roles = List.of();
        }
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();
    }
} 