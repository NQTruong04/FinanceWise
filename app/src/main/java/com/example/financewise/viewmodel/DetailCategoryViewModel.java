package com.example.financewise.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.Expense;
import com.example.financewise.data.model.Income;
import com.example.financewise.data.model.UserStats;
import com.example.financewise.data.repository.ExpenseRepository;
import com.example.financewise.data.repository.IncomeRepository;
import com.example.financewise.data.repository.UserStatsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailCategoryViewModel extends BaseViewModel{
    private static final String TAG = "DetailCategoryViewModel";
    private IncomeRepository incomeRepository;
    private ExpenseRepository expenseRepository;
    private UserStatsRepository userStatsRepository;
    private final MutableLiveData<List<HomeViewModel.TransactionItem>> transactions = new MutableLiveData<>();
    private final MutableLiveData<String> totalBalance = new MutableLiveData<>("0");
    private final MutableLiveData<String> totalExpense = new MutableLiveData<>("0");
    private final MutableLiveData<Long> totalIncome = new MutableLiveData<>(0L);
    private final MutableLiveData<Float> expensePercentage = new MutableLiveData<>(0f);
    private String userId;
    private String categoryName;
    public DetailCategoryViewModel() {
        // Default constructor
    }
    public void setUserIdAndCategory(String userId, String categoryName) {
        Log.d(TAG, "setUserIdAndCategory: userId=" + userId + ", categoryName=" + categoryName);
        if(this.userId == null || !this.userId.equals(userId) || !this.categoryName.equals(categoryName)) {
            this.userId = userId;
            this.categoryName = categoryName;
            initializeRepositories();
            loadData();
        }
    }
    private void initializeRepositories(){
        if(userId != null){
            incomeRepository = new IncomeRepository(userId);
            expenseRepository = new ExpenseRepository(userId);
            userStatsRepository = new UserStatsRepository(userId);
            Log.d(TAG, "Repositories initialized for userId: " + userId);
        }
    }

    private void loadData(){
        if(userId == null || !areRepositoriesInitialized()) {
            Log.w(TAG, "loadData called with uninitialized repositories or userId");
            return;
        }
        loadUserStats();
        loadTransactionsByCategory();
    }
    private void loadUserStats(){
        LiveData<UserStats> userStatsLiveData = userStatsRepository.getUserStats(userId);
        observeForeverWithKey("userStats", userStatsLiveData, userStats -> {
            if (userStats != null) {
                Log.d(TAG, "User stats loaded: " + userStats);
                long balance = userStats.getTotalIncome() + userStats.getTotalExpense();
                totalBalance.setValue(formatCurrency(balance));
                totalExpense.setValue(formatCurrency(userStats.getTotalExpense()));
                totalIncome.setValue(userStats.getTotalIncome());
                updateExpensePercentage(userStats.getTotalIncome(), userStats.getTotalExpense());
            } else {
                Log.w(TAG, "User stats are null");
                resetData();
            }
        }, null);
    }

    private void loadTransactionsByCategory() {
        if (categoryName == null) {
            Log.e(TAG, "categoryName is null, cannot load transactions");
            return;
        }

        MutableLiveData<List<HomeViewModel.TransactionItem>> combinedTransactions = new MutableLiveData<>();
        List<HomeViewModel.TransactionItem> transactionList = new ArrayList<>();

        // Lấy income transactions
        LiveData<List<Income>> incomeLiveData = incomeRepository.getItemsByField("category", categoryName, Income.class);
        observeForeverWithKey("incomeTransactions", incomeLiveData, incomes -> {
            if (incomes != null) {
                for (Income income : incomes) {
                    transactionList.add(new HomeViewModel.TransactionItem(income.getCategory(), income.getDate(), income.getTitle(), income.getAmount()));
                }
            }
            combineAndSortTransactions(transactionList, combinedTransactions);
        }, null);

        // Lấy expense transactions
        LiveData<List<Expense>> expenseLiveData = expenseRepository.getItemsByField("category", categoryName, Expense.class);
        observeForeverWithKey("expenseTransactions", expenseLiveData, expenses -> {
            if (expenses != null) {
                for (Expense expense : expenses) {
                    transactionList.add(new HomeViewModel.TransactionItem(expense.getCategory(), expense.getDate(), expense.getTitle(), expense.getAmount()));
                }
            }
            combineAndSortTransactions(transactionList, combinedTransactions);
        }, null);

        // Cập nhật LiveData cho UI
        transactions.setValue(transactionList);
    }
    private void combineAndSortTransactions(List<HomeViewModel.TransactionItem> transactionList, MutableLiveData<List<HomeViewModel.TransactionItem>> liveData) {
        Collections.sort(transactionList, (item1, item2) -> item2.getDate().compareTo(item1.getDate()));
        liveData.setValue(new ArrayList<>(transactionList)); // Tạo bản sao để tránh thay đổi trực tiếp
        transactions.setValue(new ArrayList<>(transactionList)); // Cập nhật transactions LiveData
    }
    private void updateExpensePercentage(long totalIncome, long totalExpense){
        if (totalIncome != 0) {
            float percentage = (float) Math.abs(totalExpense) / totalIncome * 100;
            expensePercentage.setValue(percentage);
        } else {
            expensePercentage.setValue(0f);
        }
    }
    private boolean areRepositoriesInitialized() {
        return incomeRepository != null && expenseRepository != null && userStatsRepository != null;
    }
    private void resetData() {
        totalBalance.setValue("0");
        totalExpense.setValue("0");
        totalIncome.setValue(0L);
        expensePercentage.setValue(0f);
        transactions.setValue(new ArrayList<>());
        Log.d(TAG, "Data reset to default values");
    }

    private String formatCurrency(long amount) {
        return String.format("$%,d", amount);
    }

    public LiveData<List<HomeViewModel.TransactionItem>> getTransactions() {
        return transactions;
    }

    public LiveData<String> getTotalBalance() {
        return totalBalance;
    }

    public LiveData<String> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<Float> getExpensePercentage() {
        return expensePercentage;
    }

}

