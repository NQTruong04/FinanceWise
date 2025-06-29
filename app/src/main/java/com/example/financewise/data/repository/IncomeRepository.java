package com.example.financewise.data.repository;

import androidx.lifecycle.LiveData;

import com.example.financewise.data.model.Income;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class IncomeRepository extends BaseFirestoreRepository<Income> {
    public static final String TAG = "IncomeRepository";
    public IncomeRepository(String userId){
        super("incomes", userId);
    }
    public void addIncome(Income income, OnCompleteListener<DocumentReference> listener) {
        addItem(income, listener);
    }
    public LiveData<List<Income>> getIncomesByUserAndDateRange(long startDate, long endDate) {
        return getItemsByDateRange( "date", startDate, endDate, Income.class);
    }
    public LiveData<List<Income>> getAllItems(){
        return getAllItems(Income.class);
    }
}
