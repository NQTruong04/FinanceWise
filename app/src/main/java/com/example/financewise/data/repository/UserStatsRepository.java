package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.UserStats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserStatsRepository extends BaseFirestoreRepository<UserStats> {

    private static final String TAG = "UserStatsRepository";
    private static final String STATS_DOCUMENT_ID = "stats"; // Định nghĩa documentId cố định
    private final String currentUserId;

    public UserStatsRepository(String userId) {
        super("users", userId); // Collection cha là "users"
        this.currentUserId = userId;
    }

    public LiveData<UserStats> getUserStats(String userId) {
        MutableLiveData<UserStats> userStatsLiveData = new MutableLiveData<>();
        // Xây dựng đường dẫn chính xác từ collection "users"
        DocumentReference docRef = getUserStatsDocumentRef();
        Log.d(TAG, "Querying userStats at path: " + docRef.getPath());
        docRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().exists()) {
                            UserStats userStats = task.getResult().toObject(UserStats.class);
                            Log.d(TAG, "UserStats retrieved for userId: " + userId + ", data: " + userStats);
                            userStatsLiveData.setValue(userStats);
                        } else {
                            Log.w(TAG, "No userStats document 'stats' found for userId: " + userId + ", path: " + docRef.getPath());
                            userStatsLiveData.setValue(null);
                        }
                    } else {
                        Log.e(TAG, "Error getting userStats for userId: " + userId + ", error: " + task.getException(), task.getException());
                        userStatsLiveData.setValue(null);
                    }
                });
        return userStatsLiveData;
    }
    public DocumentReference getUserStatsDocumentRef(){
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserId)
                .collection("userStats")
                .document(STATS_DOCUMENT_ID);
    }
}