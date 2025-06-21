package com.example.financewise.data.repository;

import com.example.financewise.data.model.Income;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

public class IncomeRepository extends BaseFirestoreRepository<Income> {
    public static final String TAG = "IncomeRepository";
    public IncomeRepository(){
        super("incomes");
    }
    public void addIncome(Income income, OnCompleteListener<DocumentReference> listener) {
        addItem(income, listener);
    }
}
