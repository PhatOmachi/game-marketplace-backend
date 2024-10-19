package poly.gamemarketplacebackend.core.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import poly.gamemarketplacebackend.core.security.data.CustomUserDetails;
import poly.gamemarketplacebackend.core.security.service.TokenBlacklistService;
//import poly.java5divineshop.ConfigSecurity.Security.CustomUserDetails;
//import poly.java5divineshop.ConfigSecurity.Security.service.TokenBlacklistService;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final TokenBlacklistService tokenBlacklistService;
//    private final AccountService accountService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(CustomUserDetails user, String sessionId) {
        int expiration = 1000 * 60 * 60 * 12;
//        accountService.updateSessionId(sessionId, user.getAccount().getAccountId());
//        var result = accountService.findByUsername(user.getAccount().getUsername());
        return JWT.create()
                .withSubject(user.getUsername())
//                .withClaim("user_id", (Integer) result[0])
//                .withClaim("role_name1", (String) result[1])
//                .withClaim("role_name2", (String) result[2])
//                .withClaim("session_id", (String) result[3])
//                .withClaim("screen_ids", (String) result[4])
//                .withClaim("account_id", user.getAccount().getAccountId())
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

