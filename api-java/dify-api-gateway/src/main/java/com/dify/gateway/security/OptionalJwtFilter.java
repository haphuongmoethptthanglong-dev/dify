package com.dify.gateway.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Optional JWT filter that populates SecurityContext when a valid token is present
 * but never rejects the request.
 *
 * Used in the bootstrap security chain (Order 2, permitAll) so that endpoints like
 * {@code /console/api/system-features} can detect whether the caller is authenticated
 * without requiring authentication.
 *
 * Almost identical to {@link JwtAuthenticationFilter} but catches ALL exceptions
 * (not just {@code UnauthorizedException}) to guarantee the request always proceeds.
 */
@Component
public class OptionalJwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(OptionalJwtFilter.class);

    private final JwtTokenService jwtTokenService;

    public OptionalJwtFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtTokenService.verify(token);

                if (claims.get("token_source") != null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String userId = claims.get("user_id", String.class);
                if (userId != null) {
                    var auth = new DifyAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                logger.trace("Optional JWT verification failed, continuing unauthenticated", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
