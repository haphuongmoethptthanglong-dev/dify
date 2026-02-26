package com.dify.gateway.security;

import com.dify.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter for console API endpoints.
 *
 * Matches Python's request_loader behavior for the 'console' and 'inner_api' blueprints:
 * extracts Bearer token from Authorization header, verifies JWT, extracts user_id claim.
 *
 * Python reference: {@code extensions/ext_login.py} lines 51-63
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtTokenService.verify(token);

            // Python checks: if token has "token_source" claim, reject it
            if (claims.get("token_source") != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = claims.get("user_id", String.class);
            if (userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            var auth = new DifyAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (UnauthorizedException e) {
            // Let the request continue unauthenticated — Spring Security
            // will handle the 401 via the entry point
        }

        filterChain.doFilter(request, response);
    }
}
