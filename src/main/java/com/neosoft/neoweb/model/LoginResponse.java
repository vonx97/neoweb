package com.neosoft.neoweb.model;

public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private String refreshToken;

    public LoginResponse(boolean success, String message,String token,String refreshToken) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.refreshToken = refreshToken;
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
}
