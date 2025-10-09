package ru.javabegin.micro.booksseller.authapi.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GlobalAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Логирование входящих запросов
        logger.info("Incoming request: {} {} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        // Установка общих заголовков безопасности
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Проверка Content-Type для POST/PUT запросов
        if (("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod()))
                && request.getContentType() != null
                && !request.getContentType().toLowerCase().contains("application/json")) {

            logger.warn("Invalid Content-Type for {} request: {}", request.getMethod(), request.getContentType());
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json");
            return;
        }

        filterChain.doFilter(request, response);

        // Логирование после обработки запроса
        logger.info("Request completed: {} {} - Status: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Пропускаем статические ресурсы и эндпоинты здоровья
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") ||
                path.contains(".") && path.matches(".*\\.(js|css|png|jpg|jpeg|gif|ico|svg)$");
    }
}