package com.example.financewise.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.financewise.data.repository.ExpenseRepository;
import com.example.financewise.data.repository.IncomeRepository;
import com.example.financewise.data.repository.UserStatsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionViewModel extends BaseViewModel{
    private static final String TAG = "TransactionViewModel";
    private String userId;
    private IncomeRepository incomeRepository;
    private ExpenseRepository expenseRepository;
    private UserStatsRepository userStatsRepository;
    private final MutableLiveData<List<HomeViewModel.TransactionItem>> transactions = new MutableLiveData<>();
    private final MutableLiveData<String> totalBlance = new MutableLiveData<>();
    private final MutableLiveData<String> totalExpense = new MutableLiveData<>("0");
    private final MutableLiveData<Long> totalIncome = new MutableLiveData<>(0L);
    private final Map<String, Observer> observers = new HashMap<>();

    public TransactionViewModel() {
        this.incomeRepository = null;
        this.expenseRepository = null;
        this.userStatsRepository = null;
    }
}
