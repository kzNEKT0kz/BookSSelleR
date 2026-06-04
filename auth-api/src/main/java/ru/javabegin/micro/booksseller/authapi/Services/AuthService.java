package ru.javabegin.micro.booksseller.authapi.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javabegin.micro.booksseller.authapi.DTO.AuthorizationRequest;
import ru.javabegin.micro.booksseller.authapi.DTO.AuthorizationResponse;
import ru.javabegin.micro.booksseller.authapi.DTO.RegistrationRequest;
import ru.javabegin.micro.booksseller.authapi.Entities.Admin;
import ru.javabegin.micro.booksseller.authapi.Entities.Role;
import ru.javabegin.micro.booksseller.authapi.Entities.User;
import ru.javabegin.micro.booksseller.authapi.Repositories.AdminRepository;
import ru.javabegin.micro.booksseller.authapi.Repositories.RoleRepository;
import ru.javabegin.micro.booksseller.authapi.Repositories.UserRepository;
import ru.javabegin.micro.booksseller.authapi.Security.JwtTokenProvider;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            AdminRepository adminRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthorizationResponse registerUser(
            RegistrationRequest request
    ) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()
                || adminRepository.findByEmail(request.getEmail()).isPresent()) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new RuntimeException("ROLE_USER not found"));

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setBirth(request.getBirth());
        user.setAge(request.getAge());

        user.setTotalSpendAmount(0f);

        user.setProvider("LOCAL");

        user.setRole(role);

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                role.getName()
        );

        return new AuthorizationResponse(
                token,
                user.getId(),
                user.getEmail(),
                role.getName()
        );
    }

    @Transactional
    public AuthorizationResponse registerAdmin(
            RegistrationRequest request
    ) {

        if (adminRepository.findByEmail(request.getEmail()).isPresent()
                || userRepository.findByEmail(request.getEmail()).isPresent()) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        Role role = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() ->
                        new RuntimeException("ROLE_ADMIN not found"));

        Admin admin = new Admin();

        admin.setName(request.getName());

        admin.setEmail(request.getEmail());

        admin.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        admin.setProvider("LOCAL");

        admin.setRole(role);

        admin = adminRepository.save(admin);

        String token = jwtTokenProvider.generateToken(
                admin.getId(),
                admin.getEmail(),
                role.getName()
        );

        return new AuthorizationResponse(
                token,
                admin.getId(),
                admin.getEmail(),
                role.getName()
        );
    }

    public AuthorizationResponse loginUser(
            AuthorizationRequest request
    ) {

        User user = userRepository.findByEmail(
                        request.getEmail()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        if (!"LOCAL".equals(user.getProvider())) {

            throw new IllegalArgumentException(
                    "Use OAuth2 login"
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Invalid password"
            );
        }

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        return new AuthorizationResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );
    }

    public AuthorizationResponse loginAdmin(
            AuthorizationRequest request
    ) {

        Admin admin = adminRepository.findByEmail(
                        request.getEmail()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Admin not found"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                admin.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Invalid password"
            );
        }

        String token = jwtTokenProvider.generateToken(
                admin.getId(),
                admin.getEmail(),
                admin.getRole().getName()
        );

        return new AuthorizationResponse(
                token,
                admin.getId(),
                admin.getEmail(),
                admin.getRole().getName()
        );
    }

}