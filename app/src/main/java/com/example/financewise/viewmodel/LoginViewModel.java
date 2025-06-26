package com.example.financewise.viewmodel;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.financewise.data.model.User;
import com.example.financewise.data.repository.AuthRepository;
import com.example.financewise.data.repository.UserRepository;
import com.example.financewise.view.auth.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends BaseViewModel {
    private static final String TAG = "LoginViewModel";
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final AuthRepository authRepository;
    private UserRepository userRepository;


    public LoginViewModel() {
        this.authRepository = new AuthRepository();
        this.userRepository = null;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<User> getUser(){
        return user;
    }

    public void login(String email, String password){
        if(!isValidEmail(email) || !isValidPassword(password)){
            loginResult.setValue(new LoginResult(false, getValidError(email, password)));
            return;
        }
        isLoading.setValue(true);
        authRepository.login(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser fbUser = authRepository.getCurrentUser();
                    if (fbUser != null) {
                        String userId = fbUser.getUid();
                        userRepository = new UserRepository(userId);
                        userRepository.getItem(userId, User.class).observeForever(userFromDb ->{
                            if(userFromDb!= null){
                                user.setValue(userFromDb);
                            }
                            loginResult.postValue(new LoginResult(true, "Login successful"));
                        });
                    }else{
                        loginResult.postValue(new LoginResult(false, "Login failed, user not found"));
                    }
                }else {
                    loginResult.postValue(new LoginResult(false, task.getException().getMessage()));
                }
                isLoading.postValue(false);
            }
        });
    }
    private boolean isValidEmail(String email){
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 6;
    }
    private String getValidError(String email, String password){
        if (email.isEmpty()) return "Email cannot be empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid email format";
        if (password.isEmpty()) return "Password cannot be empty";
        if (password.length() < 6) return "Password must be at least 6 characters";
        return "Unknown error";
    }
}
