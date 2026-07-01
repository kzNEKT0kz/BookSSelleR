package ru.javabegin.micro.booksseller.commonsecurity.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class JwtTokenProvider {

    private final String secret;
    private final long expiration;

    public JwtTokenProvider(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(
            Long userId,
            String email,
            List<String> roles
    ) {

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Long getUserId(String token) {

        Object value = getClaims(token).get("userId");

        if (value instanceof Integer i) {
            return i.longValue();
        }

        if (value instanceof Long l) {
            return l;
        }

        return Long.parseLong(value.toString());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {

        return getClaims(token)
                .get("roles", List.class);
    }

    public String getEmail(String token) {

        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (SecurityException |
                 JwtException |
                 IllegalArgumentException ex) {

            return false;
        }
    }

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

}