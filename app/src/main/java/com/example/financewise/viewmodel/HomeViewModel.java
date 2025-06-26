package com.example.financewise.viewmodel;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.financewise.data.model.Expense;
import com.example.financewise.data.model.Income;
import com.example.financewise.data.model.UserStats;
import com.example.financewise.data.repository.ExpenseRepository;
import com.example.financewise.data.repository.IncomeRepository;
import com.example.financewise.data.repository.UserStatsRepository;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for handling home screen data and transactions.
 */
public class HomeViewModel extends BaseViewModel {
    private static final String TAG = "HomeViewModel";
    private String userId;
    private IncomeRepository incomeRepository;
    private ExpenseRepository expenseRepository;
    private UserStatsRepository userStatsRepository;
    private final MutableLiveData<List<TransactionItem>> transactions = new MutableLiveData<>();
    private final MutableLiveData<String> totalBalance = new MutableLiveData<>("0");
    private final MutableLiveData<String> totalExpense = new MutableLiveData<>("0");
    private final MutableLiveData<Long> totalIncome = new MutableLiveData<>(0L); // New field for totalIncome from UserStats
    private final MutableLiveData<Long> totalIncomeLastWeek = new MutableLiveData<>(0L);
    private final MutableLiveData<Long> totalFoodLastWeek = new MutableLiveData<>(0L);
    private final MutableLiveData<Float> expensePercentage = new MutableLiveData<>(0f);
    private String selectedPeriod = "daily";

    // Map to store LiveData and their observers for manual cleanup
    private final Map<String, Observer<?>> observers = new HashMap<>();

    public HomeViewModel() {
        this.incomeRepository = null;
        this.expenseRepository = null;
        this.userStatsRepository = null;
    }

    public void setUserId(String userId) {
        Log.d(TAG, "setUserId: " + userId);
        if (this.userId == null || !this.userId.equals(userId)) {
            this.userId = userId;
            initializeRepositories();
            loadData(); // Load data immediately when userId is set
        }
    }

    public LiveData<List<TransactionItem>> getTransactions() {
        return transactions;
    }

    public LiveData<String> getTotalBalance() {
        return totalBalance;
    }

    public LiveData<String> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<Long> getTotalIncome() { // New getter for totalIncome
        return totalIncome;
    }

    public LiveData<Long> getTotalIncomeLastWeek() {
        return totalIncomeLastWeek;
    }

    public LiveData<Long> getTotalFoodLastWeek() {
        return totalFoodLastWeek;
    }

    public LiveData<Float> getExpensePercentage() {
        return expensePercentage;
    }

    public void setSelectedPeriod(String period) {
        Log.d(TAG, "setSelectedPeriod: " + period);
        if (!selectedPeriod.equals(period)) {
            this.selectedPeriod = period;
            loadData(); // Reload data when period changes
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
            Log.e(TAG, "userId is null or repositories not initialized");
            resetData();
            return;
        }

        cleanupObservers(); // Cleanup before loading new data
        loadUserStats();
        loadTransactions();
        loadLastWeekData();
    }

    private void loadUserStats() {
        LiveData<UserStats> userStatsLiveData = userStatsRepository.getUserStats(userId);
        Observer<UserStats> observer = userStats -> {
            Log.d(TAG, "userStats received: " + (userStats != null));
            if (userStats != null) {
                long balance = userStats.getTotalIncome() + userStats.getTotalExpense();
                totalBalance.setValue(formatCurrency(balance));
                totalExpense.setValue(formatCurrency(userStats.getTotalExpense()));
                totalIncome.setValue(userStats.getTotalIncome()); // Update totalIncome from UserStats
                updateExpensePercentage(userStats.getTotalIncome(), userStats.getTotalExpense());
            } else {
                Log.w(TAG, "No userStats found for userId: " + userId);
                resetData();
            }
        };
        observeForeverWithKey("userStats", userStatsLiveData, observer, null);
        observers.put("userStats", observer);
    }

    private void loadTransactions() {
        long startDate = getStartDateForPeriod();
        long endDate = new Date().getTime();
        Log.d(TAG, "Loading data for period: " + selectedPeriod + ", startDate: " + new Date(startDate) + ", endDate: " + new Date(endDate));

        LiveData<List<Income>> incomes = incomeRepository.getIncomesByUserAndDateRange(startDate, endDate);
        LiveData<List<Expense>> expenses = expenseRepository.getExpensesByUserAndDateRange(startDate, endDate);

        Observer<List<Income>> incomeObserver = incomeList -> {
            List<TransactionItem> transactionList = processIncomes(incomeList);
            expenses.observeForever(expenseList -> {
                transactionList.addAll(processExpenses(expenseList));
                Collections.sort(transactionList, (item1, item2) -> item2.getDate().compareTo(item1.getDate()));
                transactions.setValue(new ArrayList<>(transactionList));
            });
        };
        Observer<List<Expense>> expenseObserver = expenseList -> {
            // This observer is already handled by the nested observeForever above
        };

        observeForeverWithKey("incomes", incomes, incomeObserver, null);
        observeForeverWithKey("expenses", expenses, expenseObserver, null);
        observers.put("incomes", incomeObserver);
        observers.put("expenses", expenseObserver);
    }

    private void loadLastWeekData() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long lastWeekStart = cal.getTimeInMillis();
        long lastWeekEnd = new Date().getTime();

        LiveData<List<Income>> incomesLastWeek = incomeRepository.getIncomesByUserAndDateRange(lastWeekStart, lastWeekEnd);
        LiveData<List<Expense>> expensesLastWeek = expenseRepository.getExpensesByUserAndDateRange(lastWeekStart, lastWeekEnd);

        Observer<List<Income>> incomeLastWeekObserver = incomeList -> {
            long total = incomeList != null ? incomeList.stream().mapToLong(Income::getAmount).sum() : 0L;
            totalIncomeLastWeek.setValue(total);
            Log.d(TAG, "totalIncomeLastWeek updated to: " + total);
        };
        Observer<List<Expense>> expenseLastWeekObserver = expenseList -> {
            long totalFood = expenseList != null ? expenseList.stream()
                    .filter(e -> "food".equalsIgnoreCase(e.getCategory()))
                    .mapToLong(Expense::getAmount)
                    .sum() : 0L;
            totalFoodLastWeek.setValue(totalFood);
        };

        observeForeverWithKey("incomesLastWeek", incomesLastWeek, incomeLastWeekObserver, null);
        observeForeverWithKey("expensesLastWeek", expensesLastWeek, expenseLastWeekObserver, null);
        observers.put("incomesLastWeek", incomeLastWeekObserver);
        observers.put("expensesLastWeek", expenseLastWeekObserver);
    }

    private void updateExpensePercentage(long totalIncome, long totalExpense) {
        if (totalIncome != 0) {
            float percentage = (float) Math.abs(totalExpense) / totalIncome * 100;
            expensePercentage.setValue(percentage);
        } else {
            expensePercentage.setValue(0f);
        }
    }

    private List<TransactionItem> processIncomes(List<Income> incomes) {
        List<TransactionItem> transactionList = new ArrayList<>();
        Log.d(TAG, "Incomes received, size: " + (incomes != null ? incomes.size() : 0));
        if (incomes != null) {
            for (Income income : incomes) {
                transactionList.add(new TransactionItem(income.getCategory(), income.getDate(), income.getTitle(), income.getAmount()));
            }
        }
        return transactionList;
    }

    private List<TransactionItem> processExpenses(List<Expense> expenses) {
        List<TransactionItem> transactionList = new ArrayList<>();
        Log.d(TAG, "Expenses received, size: " + (expenses != null ? expenses.size() : 0));
        if (expenses != null) {
            for (Expense expense : expenses) {
                transactionList.add(new TransactionItem(expense.getCategory(), expense.getDate(), expense.getTitle(), expense.getAmount()));
            }
        }
        return transactionList;
    }

    private boolean areRepositoriesInitialized() {
        return incomeRepository != null && expenseRepository != null && userStatsRepository != null;
    }

    private void resetData() {
        transactions.setValue(new ArrayList<>());
        totalBalance.setValue("0");
        totalExpense.setValue("0");
        totalIncome.setValue(0L);
        totalIncomeLastWeek.setValue(0L);
        totalFoodLastWeek.setValue(0L);
        expensePercentage.setValue(0f);
    }

    @Override
    protected void cleanupObservers() {
        observers.forEach((key, observer) -> {
            if (observer != null) {
                for (LiveData<?> liveData : getLiveDataInstances()) {
                    if (liveData.hasObservers()) {
                    }
                }
                Log.d(TAG, "Removed observer for key: " + key);
            }
        });
        observers.clear();
    }

    @Override
    protected LiveData<?>[] getLiveDataInstances() {
        return new LiveData<?>[]{
                transactions, totalBalance, totalExpense, totalIncome,
                totalIncomeLastWeek, totalFoodLastWeek, expensePercentage
        };
    }

    private long getStartDateForPeriod() {
        Calendar calendar = Calendar.getInstance();
        if ("weekly".equals(selectedPeriod)) {
            calendar.add(Calendar.DAY_OF_YEAR, -7);
        } else if ("monthly".equals(selectedPeriod)) {
            calendar.add(Calendar.MONTH, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private String formatCurrency(long amount) {
        return String.format("%,d", amount);
    }

    public static class TransactionItem {
        private final String category;
        private final Timestamp date;
        private final String title;
        private final long amount;

        public TransactionItem(String category, Timestamp date, String title, long amount) {
            this.category = category;
            this.date = date;
            this.title = title;
            this.amount = amount;
        }

        public String getCategory() { return category; }
        public Timestamp getDate() { return date; }
        public String getTitle() { return title; }
        public long getAmount() { return amount; }
    }
}