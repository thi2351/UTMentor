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

        // Không có token -> cho đi tiếp; endpoint cần auth sẽ bị 401 từ EntryPoint
        if (token == null || token.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // parse + verify; ném ExpiredJwtException nếu hết hạn
            var claims = jwtService.parseAndValidate(token);

            String usernameOrId = claims.getSubject();

            // Lấy roles từ claims (List<String> hoặc String csv)
            var authorities = jwtService.extractAuthorities(claims); // xem bên dưới

            var auth = new UsernamePasswordAuthenticationToken(
                    usernameOrId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            //Token hết hạn trả isExpired = true
            SecurityContextHolder.clearContext();
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "{\"isExpired\":true}");
        } catch (io.jsonwebtoken.JwtException ex) {
            SecurityContextHolder.clearContext();
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, null);
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

    private void writeJson(HttpServletResponse res, int status, String json) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(json);
    }
}

