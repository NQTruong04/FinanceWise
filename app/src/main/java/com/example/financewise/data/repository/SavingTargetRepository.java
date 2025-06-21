package com.example.financewise.data.repository;

import com.example.financewise.data.model.Saving;
import com.example.financewise.data.model.SavingTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

public class SavingTargetRepository extends BaseFirestoreRepository<SavingTarget>{

    protected SavingTargetRepository() {
        super("savingTargets");
    }

    public void addSavingTarget(SavingTarget savingTarget, OnCompleteListener<DocumentReference> listener) {
        addItem(savingTarget, listener);
    }

    public void updateSavingTargetAmount(String documentId, double newAmount, OnCompleteListener<Void> listener) {
       getDocumentReference(documentId).update("amountSaved", newAmount).addOnCompleteListener(listener);
    }

    public void addSaving(String savingTargetId, Saving saving, OnCompleteListener<DocumentReference> listener) {
        getDocumentReference(savingTargetId).collection("savings").add(saving).addOnCompleteListener(listener);
    }
}
