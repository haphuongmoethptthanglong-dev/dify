package com.dify.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.dify.common.exception.UnauthorizedException;

/**
 * JWT token service compatible with Python's PassportService.
 *
 * Uses HS256 with the same SECRET_KEY so tokens issued by either the Python
 * or Java stack are valid during the dual-stack migration period.
 *
 * Python reference: {@code libs/passport.py}
 */
@Service
public class JwtTokenService {

    private final SecretKey signingKey;

    public JwtTokenService(@Value("${dify.security.secret-key}") String secretKey) {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Issue a JWT token with the given payload claims.
     * Matches Python: {@code PassportService().issue(payload)}
     */
    public String issue(Map<String, Object> payload) {
        var builder = Jwts.builder().claims(payload);
        if (payload.containsKey("exp")) {
            Object exp = payload.get("exp");
            if (exp instanceof Number n) {
                builder.expiration(new Date(n.longValue() * 1000));
            }
        }
        return builder.signWith(signingKey, Jwts.SIG.HS256).compact();
    }

    /**
     * Verify and decode a JWT token.
     * Matches Python: {@code PassportService().verify(token)}
     *
     * @throws UnauthorizedException if the token is invalid or expired
     */
    public Claims verify(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired.");
        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid token.");
        }
    }
}
