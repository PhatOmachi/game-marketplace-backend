package poly.gamemarketplacebackend.core.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import poly.gamemarketplacebackend.core.security.data.CustomUserDetails;
import poly.gamemarketplacebackend.core.security.service.TokenBlacklistService;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(CustomUserDetails user, String sessionId) {
        int expiration = 1000 * 60 * 60 * 12;
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getAuthorities().iterator().next().getAuthority())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public DecodedJWT verifyToken(String token) {
        return isTokenBlacklisted(token)
                ? null
                : JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
    }

    public String extractUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return verifyToken(token).getExpiresAt().before(new Date());
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistService.isTokenBlacklisted(token);
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}

