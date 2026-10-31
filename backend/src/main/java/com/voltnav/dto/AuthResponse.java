package com.voltnav.dto;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private String role;
    private String email;
    private String name;
    private Long id;

    public AuthResponse() {
    }

    public AuthResponse(String token, String role, String email, String name, Long id) {
        this.token = token;
        this.tokenType = "Bearer";
        this.role = role;
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
