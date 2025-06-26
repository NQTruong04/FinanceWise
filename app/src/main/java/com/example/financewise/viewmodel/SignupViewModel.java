package com.example.financewise.viewmodel;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.User;
import com.example.financewise.data.repository.AuthRepository;
import com.example.financewise.data.repository.UserRepository;
import com.example.financewise.view.auth.SignupResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

public class SignupViewModel extends BaseViewModel {
    private static final String TAG = "SignupViewModel";
    private final MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final AuthRepository authRepository;
    private UserRepository userRepository;

    public SignupViewModel() {
        this.authRepository = new AuthRepository();
        this.userRepository = new UserRepository(null);
    }

    public LiveData<SignupResult> getSignupResult() {
        return signupResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void signup(String name, String email, String phone, String password, String confirmPassword) {
        if (!isValidName(name) || !isValidEmail(email) || !isValidPhone(phone) || !isValidPassword(password) || !password.equals(confirmPassword)) {
            signupResult.setValue(new SignupResult(false, getValidationError(name, email, phone, password, confirmPassword)));
            return;
        }

        isLoading.setValue(true);
        authRepository.register(email, password, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = authRepository.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    userRepository = new UserRepository(userId);
                    User newUser = new User(userId, name, email, phone, 0);
                    userRepository.createUser(newUser, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                signupResult.postValue(new SignupResult(true, "Signup successful"));
                            } else {
                                signupResult.postValue(new SignupResult(false, "Failed to create user profile: " + task1.getException().getMessage()));
                            }
                            isLoading.setValue(false); // Đặt lại isLoading sau khi hoàn tất
                        }
                    });
                } else {
                    signupResult.postValue(new SignupResult(false, "Signup failed, user not found"));
                    isLoading.setValue(false);
                }
            } else {
                signupResult.postValue(new SignupResult(false, task.getException().getMessage()));
                isLoading.setValue(false);
            }
        });
    }

    private boolean isValidName(String name) {
        return !name.isEmpty() && name.length() >= 2;
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return !phone.isEmpty() && phone.matches("\\d{10}"); // 10 digits
    }

    private boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 6;
    }

    private String getValidationError(String name, String email, String phone, String password, String confirmPassword) {
        if (name.isEmpty()) return "Name cannot be empty";
        if (name.length() < 2) return "Name must be at least 2 characters";
        if (email.isEmpty()) return "Email cannot be empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid email format";
        if (phone.isEmpty()) return "Phone number cannot be empty";
        if (!phone.matches("\\d{10}")) return "Phone number must be 10 digits";
        if (password.isEmpty()) return "Password cannot be empty";
        if (password.length() < 6) return "Password must be at least 6 characters";
        if (!password.equals(confirmPassword)) return "Passwords do not match";
        return "Unknown error";
    }
}