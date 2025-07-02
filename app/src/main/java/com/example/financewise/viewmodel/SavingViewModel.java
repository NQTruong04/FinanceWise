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

public class SavingViewModel extends BaseViewModel{
    private static final String TAG = "SavingViewModel";
    private IncomeRepository incomeRepository;
    private ExpenseRepository expenseRepository;
    private UserStatsRepository userStatsRepository;
    private final MutableLiveData<List<CategoriesViewModel.CategoryItem>> savings = new MutableLiveData<>();
    private final MutableLiveData<String> totalBalance = new MutableLiveData<>("0");
    private final MutableLiveData<String> totalExpense = new MutableLiveData<>("0");
    private final MutableLiveData<Long> totalIncome = new MutableLiveData<>(0L);
    private final MutableLiveData<Float> expensePercentage = new MutableLiveData<>(0f);
    private String userId;
    private final Map<String, Observer<?>> observers = new HashMap<>();

    public SavingViewModel() {
        loadSavings();
    }
    private void loadSavings() {
        List<CategoriesViewModel.CategoryItem> savingList = new ArrayList<>();
        String[] savingNames = {"Travel", "Car", "Wedding", "House"};
        int[] savingIcons = {
                R.drawable.ic_travel_white, R.drawable.ic_car, R.drawable.ic_wedding_white,
                R.drawable.ic_house_white
        };
        for (int i = 0; i < savingNames.length; i++) {
            savingList.add(new CategoriesViewModel.CategoryItem(savingNames[i], savingIcons[i]));
        }
        savings.setValue(savingList);
        Log.d(TAG, "SavingViewModel initialized with savings: " + savingList.size());
    }

    public void setUserId(String userId) {
        Log.d(TAG, "setUserId: " + userId);
        if (this.userId == null || !this.userId.equals(userId)) {
            this.userId = userId;
            initializeRepositories();
            loadData();
        }
    }

    private void initializeRepositories() {
        if (userId != null) {
            incomeRepository = new IncomeRepository(userId);
            expenseRepository = new ExpenseRepository(userId);
            userStatsRepository = new UserStatsRepository(userId);
            Log.d(TAG, "Repositories initialized for userId: " + userId);
        }
    }

    private void loadData() {
        if (userId == null || !areRepositoriesInitialized()) {
            Log.e(TAG, "Repositories not initialized or userId is null");
            resetData();
            return;
        }
        loadUserStats();
    }

    private void loadUserStats() {
        LiveData<UserStats> userStatsLiveData = userStatsRepository.getUserStats(userId);
        Observer<UserStats> observer = userStats -> {
            Log.d(TAG, "UserStats observed: " + (userStats != null));
            if (userStats != null) {
                long balance = userStats.getTotalIncome() + userStats.getTotalExpense();
                totalBalance.setValue(formatCurrency(balance));
                totalExpense.setValue(formatCurrency(userStats.getTotalExpense()));
                totalIncome.setValue(userStats.getTotalIncome());
                updateExpensePercentage(userStats.getTotalIncome(), userStats.getTotalExpense());
            } else {
                Log.w(TAG, "UserStats is null, resetting data");
                resetData();
            }
        };
        observeForeverWithKey("userStats", userStatsLiveData, observer, null);
        observers.put("userStats", observer);
    }

    private void updateExpensePercentage(long totalIncome, long totalExpense) {
        if (totalIncome != 0) {
            float percentage = (float) Math.abs(totalExpense) / totalIncome * 100;
            expensePercentage.setValue(percentage);
            Log.d(TAG, "Expense percentage updated: " + percentage);
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
        Log.d(TAG, "Data reset to default values");
    }

    private String formatCurrency(long amount) {
        return String.format("$%,d", amount);
    }

    public LiveData<List<CategoriesViewModel.CategoryItem>> getSavings() {
        return savings;
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
}
