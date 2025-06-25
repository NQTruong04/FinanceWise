package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BaseFirestoreRepository<T> {
    private static final String TAG = "BaseFirestoreRepository";
    protected final FirebaseFirestore firestore;
    protected final CollectionReference collectionReference;

    protected BaseFirestoreRepository(String collectionName) {
        this.firestore = FirebaseFirestore.getInstance();
        this.collectionReference = firestore.collection(collectionName);
    }

    public void addItem(T item, OnCompleteListener<DocumentReference> listener) {
        collectionReference.add(item).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                listener.onComplete(task1);
            } else {
                listener.onComplete(task1);
            }
        });
    }

    public LiveData<T> getItem(String documentId, Class<T> itemClass) {
        MutableLiveData<T> itemLiveData = new MutableLiveData<>();
        collectionReference.document(documentId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        T item = task.getResult().toObject(itemClass);
                        itemLiveData.setValue(item);
                    } else {
                        Log.e(TAG, "Error getting item: ", task.getException());
                        itemLiveData.setValue(null); // Error or no result
                    }
                });
        return itemLiveData;
    }

    public void updateItem(String documentId, T item, OnCompleteListener<Void> listener) {
        collectionReference.document(documentId).set(item, com.google.firebase.firestore.SetOptions.merge()).addOnCompleteListener(listener);
    }

    public LiveData<List<T>> getItemsByField(String fieldName, Object value, Class<T> itemClass) {
        MutableLiveData<List<T>> itemsLiveData = new MutableLiveData<>();
        collectionReference.whereEqualTo(fieldName, value).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<T> items = task.getResult().toObjects(itemClass);
                        itemsLiveData.setValue(items);
                    } else {
                        Log.e(TAG, "Error getting items by field: ", task.getException());
                        itemsLiveData.setValue(null); // Error or no result
                    }
                });
        return itemsLiveData;
    }

    public LiveData<List<T>> getItemByDateRange(String userId, String dataField, long startDate, long endDate, Class<T> itemClass) {
        MutableLiveData<List<T>> itemsLiveData = new MutableLiveData<>();
        collectionReference.whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo(dataField, new java.util.Date(startDate))
                .whereLessThanOrEqualTo(dataField, new java.util.Date(endDate))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<T> items = task.getResult().toObjects(itemClass);
                        itemsLiveData.setValue(items);
                    } else {
                        Log.e(TAG, "Error getting items by date range: ", task.getException());
                        itemsLiveData.setValue(null);
                    }
                });
        return itemsLiveData;
    }

    public DocumentReference getDocumentReference(String documentId) {
        return collectionReference.document(documentId);
    }
}
