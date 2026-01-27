package ru.javabegin.micro.booksseller.authapi.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
public class AuthService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AdminRepository adminRepository , UserRepository userRepository , RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;

    }

    public AuthorizationResponse registerAdmin(RegistrationRequest request) {

        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        admin.setRole_name("ROLE_ADMIN");

        adminRepository.save(admin);
        request.setRole_name("ROLE_ADMIN");
        return register(request, admin.getId());
    }

    public AuthorizationResponse registerUser(RegistrationRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setBirth(request.getBirth());
        user.setAge(request.getAge());
        user.setTotal_spend_amount(0f);

        userRepository.save(user);
        request.setRole_name("ROLE_USER");
        return register(request, user.getId());

    }

    private AuthorizationResponse register(RegistrationRequest request, Long entity_id) {
        Role role = new Role();
        role.setEntity_Id(entity_id);
        role.setRole(request.getRole_name());
        roleRepository.save(role);

        String token = jwtTokenProvider.generateToken(request.getEmail(),request.getRole_name());

        return new AuthorizationResponse(token);
    }

    public AuthorizationResponse loginAdmin(AuthorizationRequest request) {
        Optional<Admin> admin = adminRepository.findByEmail(request.getEmail());
        if (admin.isEmpty()) {
            throw new IllegalArgumentException("Admin not found");
        }

        // Берем роль из сущности Admin
        String encodedPassword = admin.get().getPassword();
        String roleName = admin.get().getRole_name(); // ✅ здесь есть "ROLE_ADMIN"

        if (!passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(request.getEmail(), roleName);
        return new AuthorizationResponse(token);
    }


    public AuthorizationResponse loginUser(AuthorizationRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        // Берем роль из таблицы Role, где entity_id = user.id
        String roleName = roleRepository.findEntityById(user.get().getId())
                .map(Role::getRole)
                .orElse("ROLE_USER");

        String encodedPassword = user.get().getPassword();
        if (!passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(request.getEmail(), roleName);
        return new AuthorizationResponse(token);
    }



    private AuthorizationResponse login(AuthorizationRequest request, String password, String role_name) {
        if(!passwordEncoder.matches(request.getPassword(), password)){
            return new AuthorizationResponse("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(request.getEmail(),role_name);

        return new AuthorizationResponse(token);
    }



}
