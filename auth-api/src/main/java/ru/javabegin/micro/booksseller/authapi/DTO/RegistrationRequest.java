package ru.javabegin.micro.booksseller.authapi.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegistrationRequest {

    private String name;
    private String email;
    private String password;

    private LocalDate birth;
    private Integer age;
}
