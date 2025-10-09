package ru.javabegin.micro.booksseller.authapi.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javabegin.micro.booksseller.authapi.DTO.AuthorizationRequest;
import ru.javabegin.micro.booksseller.authapi.DTO.AuthorizationResponse;
import ru.javabegin.micro.booksseller.authapi.DTO.RegistrationRequest;
import ru.javabegin.micro.booksseller.authapi.Services.AuthService;

@RestController
@RequestMapping(path = "auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/user")
    public AuthorizationResponse registerUser(@RequestBody RegistrationRequest registrationRequest) {
        return authService.registerUser(registrationRequest);
    }

    @PostMapping("/register/admin")
    public AuthorizationResponse registerAdmin(@RequestBody RegistrationRequest registrationRequest) {
        return authService.registerAdmin(registrationRequest);
    }

    @PostMapping("/login/user")
    public AuthorizationResponse loginUser(@RequestBody AuthorizationRequest authorizationRequest) {
        return authService.loginUser(authorizationRequest);
    }

    @PostMapping("/login/admin")
    public AuthorizationResponse loginAdmin(@RequestBody AuthorizationRequest authorizationRequest) {
        return authService.loginAdmin(authorizationRequest);
    }
}
