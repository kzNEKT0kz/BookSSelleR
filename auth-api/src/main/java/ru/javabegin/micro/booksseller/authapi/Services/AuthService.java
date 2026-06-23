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

import java.util.List;


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

        checkEmail(request.getEmail());

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setBirth(request.getBirth());
        user.setAge(request.getAge());
        user.setTotalSpendAmount(0f);

        user = userRepository.save(user);

        List<String> roles = List.of("ROLE_USER");

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                roles
        );

        return new AuthorizationResponse(
                token,
                user.getId(),
                user.getEmail(),
                roles
        );
    }

    @Transactional
    public AuthorizationResponse registerAdmin(
            RegistrationRequest request
    ) {

        checkEmail(request.getEmail());

        Admin admin = new Admin();

        admin.setName(request.getName());

        admin.setEmail(request.getEmail());

        admin.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        Role role = roleRepository
                .findByName("ROLE_ADMIN")
                .orElseThrow(() ->
                        new IllegalArgumentException("ROLE_ADMIN not found"));

        admin.getRoles().add(role);

        admin = adminRepository.save(admin);

        List<String> roles =
                admin.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList();

        String token =
                jwtTokenProvider.generateToken(
                        admin.getId(),
                        admin.getEmail(),
                        roles
                );

        return new AuthorizationResponse(
                token,
                admin.getId(),
                admin.getEmail(),
                roles
        );
    }

    public AuthorizationResponse loginUser(
            AuthorizationRequest request
    ) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Invalid password"
            );
        }

        List<String> roles = List.of("ROLE_USER");

        String token =
                jwtTokenProvider.generateToken(
                        user.getId(),
                        user.getEmail(),
                        roles
                );

        return new AuthorizationResponse(
                token,
                user.getId(),
                user.getEmail(),
                roles
        );
    }

    public AuthorizationResponse loginAdmin(
            AuthorizationRequest request
    ) {

        Admin admin = adminRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Admin not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                admin.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Invalid password"
            );
        }

        List<String> roles =
                admin.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList();

        String token =
                jwtTokenProvider.generateToken(
                        admin.getId(),
                        admin.getEmail(),
                        roles
                );

        return new AuthorizationResponse(
                token,
                admin.getId(),
                admin.getEmail(),
                roles
        );
    }

    private void checkEmail(String email) {

        if (userRepository.findByEmail(email).isPresent()
                || adminRepository.findByEmail(email).isPresent()) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }
    }

}