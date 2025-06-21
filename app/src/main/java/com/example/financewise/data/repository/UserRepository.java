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

    public UserRepository() {
        super("users");
    }
    public void createUser(User user, OnCompleteListener<DocumentReference> listener){
        addItem(user, listener);
    }
    public void updateUser(String userId, User user, OnCompleteListener<Void> listener) {
        updateItem(userId, user, listener);
    }
}
