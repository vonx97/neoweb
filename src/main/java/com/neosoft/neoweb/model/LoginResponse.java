package com.neosoft.neoweb.model;

import java.util.List;

public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private String refreshToken;
    private List<String> roles;

    public LoginResponse(boolean success, String message,String token,String refreshToken,List<String> roles) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.refreshToken = refreshToken;
        this.roles = roles;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public List<String> getRoles() {
        return roles;
    }
}
