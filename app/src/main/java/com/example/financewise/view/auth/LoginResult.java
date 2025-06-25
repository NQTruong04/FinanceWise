package com.example.financewise.view.auth;

public class LoginResult {
    private final boolean success;
    private final String message;

    public LoginResult(boolean success, String errorMessage) {
        this.success = success;
        this.message = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
