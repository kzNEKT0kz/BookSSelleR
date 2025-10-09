package ru.javabegin.micro.booksseller.authapi.DTO;

public class AuthorizationResponse {


    private String token;

    public AuthorizationResponse() {
    }

    public AuthorizationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}