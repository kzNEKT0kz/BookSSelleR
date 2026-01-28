package ru.javabegin.micro.booksseller.authapi.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.javabegin.micro.booksseller.authapi.Entities.User;
import ru.javabegin.micro.booksseller.authapi.Services.AuthService; // Используем AuthService

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService; // Используем AuthService вместо UserService
    private final ObjectMapper objectMapper;

    public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider,
                                AuthService authService,
                                ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String provider = oauthToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oauthUser.getAttributes();

        // Используем AuthService для поиска/создания пользователя
        Optional<User> user = authService.findOrCreateOAuthUser(provider, attributes);

        if (user.isPresent()) {
            // Генерируем JWT токен
            String token = jwtTokenProvider.generateToken(
                    user.get().getEmail(),
                    user.get().getRole().name(),
                    "USER"
            );

            // Возвращаем токен как JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("token", token);
            tokenResponse.put("email", user.get().getEmail());
            tokenResponse.put("role", user.get().getRole().name());
            tokenResponse.put("userType", "USER");

            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth authentication failed");
        }
    }
}