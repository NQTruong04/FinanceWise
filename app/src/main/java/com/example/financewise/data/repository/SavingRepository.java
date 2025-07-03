package com.example.financewise.data.repository;

import android.util.Log;

import com.example.financewise.data.model.Saving;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

public class SavingRepository extends BaseFirestoreRepository<Saving> {

    private static final String TAG = "SavingRepository";
    private final String savingTargetId;

    public SavingRepository(String savingTargetId, String userId) {
        super("users/" + userId + "/savingTargets/" + savingTargetId + "/savings", true);
        this.savingTargetId = savingTargetId;
        Log.d(TAG, "Initialized SavingRepository for userId: " + userId + ", savingTargetId: " + savingTargetId);
    }
    public void addSaving(Saving saving, OnCompleteListener<DocumentReference> listener) {
        addItem(saving, listener);
    }
}
