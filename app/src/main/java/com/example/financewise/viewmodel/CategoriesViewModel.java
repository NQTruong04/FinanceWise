package com.example.financewise.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.financewise.R;
import com.example.financewise.data.model.UserStats;
import com.example.financewise.data.repository.ExpenseRepository;
import com.example.financewise.data.repository.IncomeRepository;
import com.example.financewise.data.repository.UserStatsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesViewModel extends BaseViewModel{
    private static final String TAG = "CategoriesViewModel";
    private IncomeRepository incomeRepository;
    private ExpenseRepository expenseRepository;
    private UserStatsRepository userStatsRepository;
    private final MutableLiveData<List<CategoryItem>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> totalBalance = new MutableLiveData<>("0");
    private final MutableLiveData<String> totalExpense = new MutableLiveData<>("0");
    private final MutableLiveData<Long> totalIncome = new MutableLiveData<>(0L);
    private final MutableLiveData<Float> expensePercentage = new MutableLiveData<>(0f);
    private String userId;

    private final Map<String, Observer<?>> observers = new HashMap<>();

    public CategoriesViewModel(){
        loadCategories();
    }

    private void loadCategories(){
        List<CategoryItem> categoryList = new ArrayList<>();
        String[] categoryNames = {"Food", "Medicine", "Transport", "Groceries", "Salary", "Rent", "Entertainment", "Gift", "Savings", "Other"};
        int[] categoryIcons = {
                R.drawable.ic_food_white, R.drawable.ic_medicine, R.drawable.ic_transport,
                R.drawable.ic_groceries, R.drawable.ic_salary, R.drawable.ic_rent,
                R.drawable.ic_entertainment, R.drawable.ic_gifts, R.drawable.ic_savings ,R.drawable.ic_more
        };
        for(int i =0; i< categoryNames.length; i++){
            categoryList.add(new CategoryItem(categoryNames[i], categoryIcons[i]));
        }
        categories.setValue(categoryList);
        Log.d(TAG, "CategoriesViewModel initialized");

    }
    public void setUserId(String userId){
        Log.d(TAG, "setUserId: " + userId);
        if(this.userId == null || !this.userId.equals(userId)){
            this.userId = userId;
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
        if(userId == null || !areRepositoriesInitialized()){
            Log.e(TAG, "Repositories not initialized or userId is null");
            resetData();
            return;
        }
        loadUserStats();
    }

    private void loadUserStats(){
        LiveData<UserStats> userStatsLiveData = userStatsRepository.getUserStats(userId);
        Observer<UserStats> observer = userStats -> {
            Log.d(TAG, "UserStats observed: " + (userStats != null));
            if(userStats != null){
                long balance = userStats.getTotalIncome() + userStats.getTotalExpense();
                totalBalance.setValue(formatCurrrency(balance));
                totalExpense.setValue(formatCurrrency(userStats.getTotalExpense()));
                totalIncome.setValue(userStats.getTotalIncome());
                updateExpensePercentage(userStats.getTotalIncome(), userStats.getTotalExpense());
            }else {
                Log.w(TAG, "UserStats is null, resetting data");
                resetData();
            }
        };
        observeForeverWithKey("userStats", userStatsLiveData, observer, null);
        observers.put("userStats", observer);
    }

    private void updateExpensePercentage(long totalIncome, long totalExpense){
        if(totalIncome != 0){
            float percentage = (float) Math.abs(totalExpense) / totalIncome * 100;
            expensePercentage.setValue(percentage);
            Log.d(TAG, "Expense percentage updated: " + percentage);
        }else {
            expensePercentage.setValue(0f);
        }
    }

    private boolean areRepositoriesInitialized(){
        return incomeRepository != null && expenseRepository != null && userStatsRepository != null;
    }
    private void resetData(){
        totalBalance.setValue("0");
        totalExpense.setValue("0");
        totalIncome.setValue(0L);
        expensePercentage.setValue(0f);
        Log.d(TAG, "Data reset to default values");
    }

    private String formatCurrrency(long amount) {
        return String.format("$%,d", amount);
    }

    public LiveData<List<CategoryItem>> getCategories() {
        return categories;
    }
    public LiveData<String> getTotalBalance() {
        return totalBalance;
    }
    public LiveData<String> getTotalExpense() {
        return totalExpense;
    }
    public LiveData<Long> getTotalIncome() {
        return totalIncome;
    }
    public LiveData<Float> getExpensePercentage() {
        return expensePercentage;
    }

    public static class CategoryItem{
        private final String name;
        private final int iconResId;

        public CategoryItem(String name, int iconResId){
            this.name = name;
            this.iconResId = iconResId;
        }
        public String getName() {
            return name;
        }
        public int getIconResId() {
            return iconResId;
        }
    }

}
