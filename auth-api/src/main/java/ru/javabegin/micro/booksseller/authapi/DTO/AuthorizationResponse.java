package ru.javabegin.micro.booksseller.authapi.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponse {

    private String token;

    private Long userId;

    private String email;

    private List<String> roles;
}