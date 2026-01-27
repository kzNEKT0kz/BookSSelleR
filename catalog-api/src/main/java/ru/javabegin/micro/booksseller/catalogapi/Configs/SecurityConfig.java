package ru.javabegin.micro.booksseller.catalogapi.Configs;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Фильтр читает заголовки X-User-Name и X-Role,
     * которые добавляет Gateway после проверки JWT.
     */
    public class GatewayAuthHeaderFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            // читаем заголовки, добавленные gateway после JWT-проверки
            String username = request.getHeader("X-User-Name");
            String role = request.getHeader("X-Role");

            if (username != null && role != null) {

                // убеждаемся, что роль имеет префикс ROLE_
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                // создаём объект Authentication
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );

                // помещаем в контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // пропускаем дальше
            filterChain.doFilter(request, response);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // вставляем фильтр до проверки авторизации
                .addFilterBefore(new GatewayAuthHeaderFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/catalog/test").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/catalog/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}