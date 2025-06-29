package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BaseFirestoreRepository<T> {
    private static final String TAG = "BaseFirestoreRepository";
    protected final FirebaseFirestore firestore;
    protected final CollectionReference collectionReference;

    protected BaseFirestoreRepository(String collectionName, String userId) {
        this.firestore = FirebaseFirestore.getInstance();
        if(userId == null){
            throw new IllegalArgumentException("userId cannot be null");
        }
        this.collectionReference = firestore.collection("users")
                .document(userId)
                .collection(collectionName);
        Log.d(TAG, "Initialized collectionReference for userId: " + userId + ", path: " + collectionReference.getPath());
    }

    public void addItem(T item, OnCompleteListener<DocumentReference> listener) {
        collectionReference.add(item).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Log.d(TAG, "Added item successfully at path: " + task1.getResult().getPath());
                listener.onComplete(task1);
            } else {
                Log.e(TAG, "Error adding item: " + task1.getException(), task1.getException());
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
                        Log.d(TAG, "Retrieved item at path: " + collectionReference.document(documentId).getPath() + ", data: " + item);
                        itemLiveData.setValue(item);
                    } else {
                        Log.e(TAG, "Error getting item at path: " + collectionReference.document(documentId).getPath() + ", error: " + task.getException(), task.getException());
                        itemLiveData.setValue(null);
                    }
                });
        return itemLiveData;
    }

    public void updateItem(String documentId, T item, OnCompleteListener<Void> listener) {
        collectionReference.document(documentId).set(item, com.google.firebase.firestore.SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Updated item at path: " + collectionReference.document(documentId).getPath());
                    } else {
                        Log.e(TAG, "Error updating item at path: " + collectionReference.document(documentId).getPath() + ", error: " + task.getException(), task.getException());
                    }
                    listener.onComplete(task);
                });
    }

    public LiveData<List<T>> getItemsByField(String fieldName, Object value, Class<T> itemClass) {
        MutableLiveData<List<T>> itemsLiveData = new MutableLiveData<>();
        collectionReference.whereEqualTo(fieldName, value).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<T> items = task.getResult().toObjects(itemClass);
                        Log.d(TAG, "Retrieved items by field at path: " + collectionReference.getPath() + ", size: " + (items != null ? items.size() : 0));
                        itemsLiveData.setValue(items);
                    } else {
                        Log.e(TAG, "Error getting items by field at path: " + collectionReference.getPath() + ", error: " + task.getException(), task.getException());
                        itemsLiveData.setValue(null);
                    }
                });
        return itemsLiveData;
    }

    public LiveData<List<T>> getItemsByDateRange( String dateField, long startDate, long endDate, Class<T> itemClass) {
        MutableLiveData<List<T>> itemsLiveData = new MutableLiveData<>();
        collectionReference.whereGreaterThanOrEqualTo(dateField, new java.util.Date(startDate))
                .whereLessThanOrEqualTo(dateField, new java.util.Date(endDate))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<T> items = task.getResult().toObjects(itemClass);
                        Log.d(TAG, "Retrieved items by date range at path: " + collectionReference.getPath() + ", size: " + (items != null ? items.size() : 0));
                        itemsLiveData.setValue(items);
                    } else {
                        Log.e(TAG, "Error getting items by date range at path: " + collectionReference.getPath() + ", error: " + task.getException(), task.getException());
                        itemsLiveData.setValue(null);
                    }
                });
        return itemsLiveData;
    }

    public DocumentReference getDocumentReference(String documentId) {
        return collectionReference.document(documentId);
    }
    public LiveData<List<T>> getAllItems(Class<T> itemClass){
        MutableLiveData<List<T>> itemsLiveData = new MutableLiveData<>();
        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        List<T> items = task.getResult().toObjects(itemClass);
                        Log.d(TAG, "Retrieved all items at path: " + collectionReference.getPath() + ", size " + (items != null ? items.size() : 0));
                        itemsLiveData.setValue(items);
                    }else {
                        Log.d(TAG, "Error setting all items at path: " + collectionReference.getPath());
                        itemsLiveData.setValue(null);
                    }
                });
    return itemsLiveData;
    }
}