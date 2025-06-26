package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.financewise.data.model.Expense;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class ExpenseRepository extends BaseFirestoreRepository<Expense> {

    public static final String TAG = "ExpenseRepository";

    public ExpenseRepository(String userId) {
        super("expenses", userId); // Truyền userId vào constructor
    }

    public void addExpense(Expense expense, OnCompleteListener<DocumentReference> listener) {
        addItem(expense, listener);
    }

    public LiveData<List<Expense>> getExpensesByUserAndDateRange( long startDate, long endDate) {
        return getItemsByDateRange("date", startDate, endDate, Expense.class);
    }
}