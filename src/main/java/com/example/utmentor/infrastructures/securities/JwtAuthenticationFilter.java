package com.example.utmentor.infrastructures.securities;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token == null || token.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            var claims = jwtService.parseAndValidate(token); // throws ExpiredJwtException on exp
            var authorities = jwtService.extractAuthorities(claims);

            var auth = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // mark as expired
            SecurityContextHolder.clearContext();
            request.setAttribute("JWT_EXPIRED", true);
            chain.doFilter(request, response);

        } catch (io.jsonwebtoken.JwtException ex) {
            // invalid token; proceed as anonymous
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (h != null && h.startsWith("Bearer ")) return h.substring(7);
        if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("accessToken".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
}


