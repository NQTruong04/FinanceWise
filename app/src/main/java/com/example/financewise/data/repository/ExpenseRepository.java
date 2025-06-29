package com.example.financewise.data.repository;

import androidx.lifecycle.LiveData;

import com.example.financewise.data.model.Expense;

import java.util.List;

public class ExpenseRepository extends BaseFirestoreRepository<Expense> {

    public static final String TAG = "ExpenseRepository";

    public ExpenseRepository(String userId) {
        super("expenses", userId);
    }

    public LiveData<List<Expense>> getExpensesByUserAndDateRange( long startDate, long endDate) {
        return getItemsByDateRange("date", startDate, endDate, Expense.class);
    }

    public LiveData<List<Expense>> getAllItems(){
        return getAllItems(Expense.class);
    }
}