package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.Expense;
import com.example.financewise.viewmodel.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseRepository extends BaseFirestoreRepository<Expense> {

    public static final String TAG = "ExpenseRepository";


    public ExpenseRepository(String userId) {
        super("expenses", userId);
    }

    public void addExpense(Expense expense, OnCompleteListener<DocumentReference> listener) {
        addItem(expense, listener);
    }

    public LiveData<List<Expense>> getExpensesByUserAndDateRange( long startDate, long endDate) {
        return getItemsByDateRange("date", startDate, endDate, Expense.class);
    }

    public LiveData<List<Expense>> getAllItems(){
        return getAllItems(Expense.class);
    }
    public LiveData<List<Expense>> getExpensesByCategory(String fieldName, Object value, Class<HomeViewModel.TransactionItem> clazz){
        MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
        Query query = collectionReference.whereEqualTo(fieldName, value);

        collectionReference.whereEqualTo(fieldName, value)
                .addSnapshotListener((value1, e) ->{
                    if(e != null){
                        Log.e(TAG, "Error getting expenses by category: " + e.getMessage(), e);
                        expensesLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if(value1 != null){
                        List<Expense> expenses = value1.toObjects(Expense.class);
                        Collections.sort(expenses, (ex1, ex2) -> ex2.getDate().compareTo(ex1.getDate()));
                        Log.d(TAG, "Expenses retrieved by category: " + expenses.size() + " items found.");
                        expensesLiveData.setValue(expenses);
                    }else{
                        Log.w(TAG, "No expenses found for category: " + value);
                        expensesLiveData.setValue(new ArrayList<>());
                    }
                });
        return expensesLiveData;
    }
}