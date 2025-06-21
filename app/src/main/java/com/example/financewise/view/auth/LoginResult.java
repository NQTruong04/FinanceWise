package com.example.financewise.view.auth;

public class LoginResult {
    private final boolean success;
    private final String Message;

    public LoginResult(boolean success, String errorMessage) {
        this.success = success;
        this.Message = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return Message;
    }
}
