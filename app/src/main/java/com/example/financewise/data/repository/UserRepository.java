package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository extends BaseFirestoreRepository<User> {
    private static final String TAG = "UserRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserRepository(String userId) {
        super("users", userId);
    }
    public void createUser(User user, OnCompleteListener<Void> listener) {
        Log.d(TAG, "Creating user with userId: " + user.getUserId());
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile created successfully for userId: " + user.getUserId());
                    } else {
                        Log.e(TAG, "Failed to create user profile: " + task.getException().getMessage());
                    }
                    if (listener != null) {
                        listener.onComplete(task);
                    }
                });
    }
    public void updateUser(String userId, User user, OnCompleteListener<Void> listener) {
        updateItem(userId, user, listener);
    }
    public LiveData<User> getUser(String userId){
        return getItem(userId, User.class);
    }
    public DocumentReference getUserDocumentRef(String userId){
        return db.collection("users").document(userId);
    }
}
