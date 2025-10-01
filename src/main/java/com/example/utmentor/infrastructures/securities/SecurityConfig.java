package com.example.utmentor.infrastructures.securities;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Stream;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final SecretKey hmacKey;

    public SecurityConfig(@Value("${jwt.secret}") String secret) {
        this.hmacKey = new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())
                .rememberMe(r -> r.disable())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(hmacKey)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();
    }

    @Bean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return (Jwt jwt) -> {
            String subject = jwt.getSubject();
            Collection<? extends GrantedAuthority> authorities = extractAuthorities(jwt);
            return new UsernamePasswordAuthenticationToken(subject, jwt.getTokenValue(), authorities);
        };
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract roles from the "roles" claim
        Object rolesClaim = jwt.getClaim("roles");
        Stream<String> roles;
        
        if (rolesClaim instanceof Collection<?> list) {
            // roles as a list of strings
            roles = list.stream().filter(String.class::isInstance).map(String.class::cast);
        } else {
            // No roles found
            roles = Stream.empty();
        }
        
        return roles.map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
    }
}