package com.example.financewise.view.auth;

public class SignupResult {
    private final boolean success;
    private final String message;

    public SignupResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
