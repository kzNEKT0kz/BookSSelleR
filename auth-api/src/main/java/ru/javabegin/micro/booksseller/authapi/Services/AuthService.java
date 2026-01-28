package ru.javabegin.micro.booksseller.authapi.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javabegin.micro.booksseller.authapi.DTO.AuthorizationRequest;
import ru.javabegin.micro.booksseller.authapi.DTO.AuthorizationResponse;
import ru.javabegin.micro.booksseller.authapi.DTO.RegistrationRequest;
import ru.javabegin.micro.booksseller.authapi.Entities.Admin;
import ru.javabegin.micro.booksseller.authapi.Entities.User;
import ru.javabegin.micro.booksseller.authapi.Repositories.AdminRepository;
import ru.javabegin.micro.booksseller.authapi.Repositories.UserRepository;
import ru.javabegin.micro.booksseller.authapi.Security.JwtTokenProvider;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AdminRepository adminRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthorizationResponse registerAdmin(RegistrationRequest request) {
        // Проверяем, нет ли уже админа с таким email
        if (adminRepository.findByEmail(request.getEmail()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        admin.setProvider("LOCAL");

        if (request.getRole_name() != null) {
            try {
                admin.setRole(Admin.Role.valueOf(request.getRole_name()));
            } catch (IllegalArgumentException e) {
                admin.setRole(Admin.Role.ROLE_ADMIN); // По умолчанию
            }
        }

        admin = adminRepository.save(admin);

        String token = jwtTokenProvider.generateToken(
                admin.getEmail(),
                admin.getRole().name(),
                "ADMIN"
        );

        return new AuthorizationResponse(token);
    }

    @Transactional
    public AuthorizationResponse registerUser(RegistrationRequest request) {
        // Проверяем, нет ли уже пользователя с таким email
        if (userRepository.findByEmail(request.getEmail()).isPresent() ||
                adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setBirth(request.getBirth());
        user.setAge(request.getAge());
        user.setTotal_spend_amount(0f);
        user.setProvider("LOCAL");

        if (request.getRole_name() != null) {
            try {
                user.setRole(User.Role.valueOf(request.getRole_name()));
            } catch (IllegalArgumentException e) {
                user.setRole(User.Role.ROLE_USER); // По умолчанию
            }
        }

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                "USER"
        );

        return new AuthorizationResponse(token);
    }

    public AuthorizationResponse loginAdmin(AuthorizationRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isEmpty()) {
            throw new IllegalArgumentException("Admin not found");
        }

        Admin admin = adminOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(
                admin.getEmail(),
                admin.getRole().name(),
                "ADMIN"
        );

        return new AuthorizationResponse(token);
    }

    public AuthorizationResponse loginUser(AuthorizationRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();

        // Проверяем пароль только для LOCAL пользователей
        if ("LOCAL".equals(user.getProvider()) &&
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name(),
                "USER"
        );

        return new AuthorizationResponse(token);
    }

    // Метод для создания OAuth пользователя
    public User createOAuthUser(String email, String name, String provider, Map<String, Object> attributes) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProvider(provider.toUpperCase());
        user.setProviderId(getProviderId(provider, attributes));
        // OAuth пользователям не нужен пароль
        user.setPassword(null);
        user.setRole(User.Role.ROLE_USER); // По умолчанию

        return userRepository.save(user);
    }

    private String getProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        } else if ("github".equals(provider)) {
            return attributes.get("id").toString();
        }
        return null;
    }

    // ==== НОВЫЕ МЕТОДЫ ДЛЯ OAUTH ====

    /**
     * Находит или создает пользователя через OAuth2
     */
    public Optional<User> findOrCreateOAuthUser(String provider, Map<String, Object> attributes) {
        String email = getEmailFromOAuthAttributes(provider, attributes);

        if (email == null) {
            return Optional.empty();
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Обновляем информацию существующего пользователя
            User user = existingUser.get();
            updateUserFromOAuth(user, provider, attributes);
            return Optional.of(userRepository.save(user));
        } else {
            // Создаем нового пользователя через OAuth
            User newUser = createUserFromOAuth(provider, attributes);
            return Optional.of(userRepository.save(newUser));
        }
    }

    /**
     * Создает нового пользователя из OAuth2 данных
     */
    public User createUserFromOAuth(String provider, Map<String, Object> attributes) {
        String email = getEmailFromOAuthAttributes(provider, attributes);
        String name = getNameFromOAuthAttributes(provider, attributes);

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProvider(provider.toUpperCase());
        user.setProviderId(getProviderIdFromOAuth(provider, attributes));
        user.setPassword(null); // OAuth пользователям не нужен пароль
        user.setRole(User.Role.ROLE_USER); // По умолчанию обычный пользователь

        return user;
    }

    /**
     * Обновляет существующего пользователя данными из OAuth2
     */
    public void updateUserFromOAuth(User user, String provider, Map<String, Object> attributes) {
        user.setName(getNameFromOAuthAttributes(provider, attributes));
        user.setProvider(provider.toUpperCase());
        user.setProviderId(getProviderIdFromOAuth(provider, attributes));
    }

    /**
     * Получает email из атрибутов OAuth2 провайдера
     */
    private String getEmailFromOAuthAttributes(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        } else if ("github".equals(provider)) {
            String email = (String) attributes.get("email");
            if (email == null && attributes.get("login") != null) {
                // Для GitHub создаем email из login, если email скрыт
                return attributes.get("login") + "@github.com";
            }
            return email;
        }
        return null;
    }

    /**
     * Получает имя из атрибутов OAuth2 провайдера
     */
    private String getNameFromOAuthAttributes(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        } else if ("github".equals(provider)) {
            return (String) attributes.get("name");
        }
        return null;
    }

    /**
     * Получает providerId из атрибутов OAuth2 провайдера
     */
    private String getProviderIdFromOAuth(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        } else if ("github".equals(provider)) {
            return attributes.get("id").toString();
        }
        return null;
    }

    /**
     * Находит пользователя по email
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Находит админа по email
     */
    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
}