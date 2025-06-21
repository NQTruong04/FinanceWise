package com.example.financewise.data.repository;

import com.example.financewise.data.model.Expense;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

public class ExpenseRepository extends BaseFirestoreRepository<Expense> {

    public static final String TAG = "ExpenseRepository";

    public ExpenseRepository() {
        super("expenses");
    }

    public void addExpense(Expense expense, OnCompleteListener<DocumentReference> listener) {
        addItem(expense, listener);
    }

}
