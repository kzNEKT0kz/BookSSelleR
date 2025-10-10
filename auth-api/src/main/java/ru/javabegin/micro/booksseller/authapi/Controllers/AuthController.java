package ru.javabegin.micro.booksseller.authapi.Controllers;

import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
