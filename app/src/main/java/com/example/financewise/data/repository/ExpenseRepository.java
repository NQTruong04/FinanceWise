package com.example.financewise.data.repository;

import androidx.lifecycle.LiveData;

import com.example.financewise.data.model.Expense;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class ExpenseRepository extends BaseFirestoreRepository<Expense> {

    public static final String TAG = "ExpenseRepository";

    public ExpenseRepository() {
        super("expenses");
    }

    public void addExpense(Expense expense, OnCompleteListener<DocumentReference> listener) {
        addItem(expense, listener);
    }
    public LiveData<List<Expense>> getExpensesByUserAndDateRange(String userId, long startDate, long endDate) {
        return getItemByDateRange(userId, "date", startDate, endDate, Expense.class);
    }
}
