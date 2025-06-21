package com.example.financewise.data.repository;

import com.example.financewise.data.source.FirebaseDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    public static final String TAG = "AuthRepository";
    public static FirebaseDataSource firebaseDataSource;

    public AuthRepository() {
        this.firebaseDataSource = new FirebaseDataSource();
    }
    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseDataSource.login(email, password, listener);
    }
    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseDataSource.register(email, password, listener);
    }
    public void logout() {
        firebaseDataSource.logout();
    }
    public FirebaseUser getCurrentUser(){
        return firebaseDataSource.getCurrentUser();
    }
}
