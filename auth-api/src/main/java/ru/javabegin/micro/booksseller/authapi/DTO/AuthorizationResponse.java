package ru.javabegin.micro.booksseller.authapi.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponse {

    private String token;

    private Long userId;

    private String email;

    private String role;
}