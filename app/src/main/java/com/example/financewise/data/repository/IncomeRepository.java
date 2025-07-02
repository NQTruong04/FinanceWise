package com.example.financewise.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.Income;
import com.example.financewise.viewmodel.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
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
    public LiveData<List<Income>> getIncomeByCategory(String fieldName, Object value, Class<HomeViewModel.TransactionItem> clazz){
        MutableLiveData<List<Income>> incomesLiveData = new MutableLiveData<>();
        Query query = collectionReference.whereEqualTo(fieldName, value);

        collectionReference.whereEqualTo(fieldName, value)
                .addSnapshotListener((value1, e) ->{
                    if(e != null){
                        Log.e(TAG, "Error getting expenses by category: " + e.getMessage(), e);
                        incomesLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if(value1 != null){
                        List<Income> incomes = value1.toObjects(Income.class);
                        Collections.sort(incomes, (ex1, ex2) -> ex2.getDate().compareTo(ex1.getDate()));
                        Log.d(TAG, "Expenses retrieved by category: " + incomes.size() + " items found.");
                        incomesLiveData.setValue(incomes);
                    }else{
                        Log.w(TAG, "No expenses found for category: " + value);
                        incomesLiveData.setValue(new ArrayList<>());
                    }
                });
        return incomesLiveData;
    }
}
