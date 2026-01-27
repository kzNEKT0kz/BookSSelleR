package ru.javabegin.micro.booksseller.gatewayapi.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // ✅ Разрешаем публичные пути (авторизация, Swagger, Eureka)
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        // ✅ Проверяем наличие Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            if (username == null || role == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // ✅ Лог для проверки
            System.out.println("✅ JWT прошёл проверку: " + username + " (" + role + ")");

            // ✅ Добавляем заголовки X-User-* в новый запрос
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Name", username)
                    .header("X-Role", role)
                    .build();

            // ✅ Передаём дальше
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (JwtException e) {
            System.out.println("❌ JWT ошибка: " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicEndpoint(String path) {
        String lower = path.toLowerCase();
        return lower.startsWith("/auth")
                || lower.startsWith("/auth-api")
                || lower.startsWith("/swagger")
                || lower.startsWith("/v3/api-docs")
                || lower.startsWith("/eureka");
    }

    @Override
    public int getOrder() {
        return -1; // Важно: фильтр должен выполняться раньше остальных
    }
}
