package ar.com.velascosoft.haylugar.util.jwt;

import ar.com.velascosoft.haylugar.util.EncodeUtil;
import ar.com.velascosoft.haylugar.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret:}")
    private String secret;

    public String generateToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(subject, claims);
    }

    public String generateToken(String subject, Map<String, Object> claims) {

        if(StringUtils.isNullOrEmpty(secret))
            throw new IllegalStateException("Secret is not defined !!!");

        final long EXPIRATION_TIME = 5000 * 60 * 60; //5 hours
        final Date issuedTime = new Date(System.currentTimeMillis());
        final Date expirationTime = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(issuedTime)
                .expiration(expirationTime)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        if(StringUtils.isNullOrEmpty(secret))
            throw new IllegalStateException("Secret is not defined !!!");

        JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();

        return parser
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String jwt) {
        return !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] bytes = EncodeUtil.encodeBase64FromString(secret);
        return Keys.hmacShaKeyFor(bytes);
    }
}
