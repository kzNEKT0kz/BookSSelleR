package ru.javabegin.micro.booksseller.authapi.DTO;

public class AuthorizationResponse {


    private String token;
    private String email;
    private String role;
    private String userType;

    public AuthorizationResponse(String token) {
    }

    public AuthorizationResponse(String token, String email, String role, String userType) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


    @Override
    public String toString() {
        return "AuthorizationResponse{" +
                "token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}