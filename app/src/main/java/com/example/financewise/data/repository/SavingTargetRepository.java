package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.Saving;
import com.example.financewise.data.model.SavingTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class SavingTargetRepository extends BaseFirestoreRepository<SavingTarget> {

    private static final String TAG = "SavingTargetRepository";

    public SavingTargetRepository(String userId) {
        super("savingTargets", userId); // Truyền userId vào constructor
    }

    public void addSavingTarget(SavingTarget savingTarget, OnCompleteListener<DocumentReference> listener) {
        addItem(savingTarget, listener);
    }

    public void updateSavingTargetAmount(String documentId, double newAmount, OnCompleteListener<Void> listener) {
        getDocumentReference(documentId).update("amountSaved", newAmount).addOnCompleteListener(listener);
    }
    public LiveData<SavingTarget> getSavingTargetByCategory(String category, String userId){
        MutableLiveData<SavingTarget> targetLiveData = new MutableLiveData<>();
        collectionReference.whereEqualTo("category", category)
                .limit(1)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult() != null){
                        if(!task.getResult().isEmpty()){
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            SavingTarget target = document.toObject(SavingTarget.class);
                            Log.d(TAG, "Retrieved saving target by category: " + category + ", data: " + target);
                            targetLiveData.setValue(target);
                        }else{
                            Log.d(TAG, "No saving target found for category: " + category);
                            targetLiveData.setValue(null);
                        }
                    }else {
                        Log.e(TAG, "Error getting saving target by category: " + category + ", error: " + task.getException(), task.getException());
                        targetLiveData.setValue(null);
                    }
                });
        return targetLiveData;
    }
}