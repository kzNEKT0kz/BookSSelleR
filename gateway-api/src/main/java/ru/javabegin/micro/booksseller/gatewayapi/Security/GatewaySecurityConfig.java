package ru.javabegin.micro.booksseller.gatewayapi.Security;


import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig  {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // 💥 отключаем CSRF
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**", "/auth-api/**").permitAll() // разрешаем доступ к /auth без авторизации
                        .anyExchange().authenticated()
                )
                .build();
    }
}
