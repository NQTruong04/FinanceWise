package com.example.financewise.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.Expense;
import com.example.financewise.data.model.Income;
import com.example.financewise.data.repository.ExpenseRepository;
import com.example.financewise.data.repository.IncomeRepository;
import com.example.financewise.data.repository.UserStatsRepository;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends BaseViewModel {
    private static final String TAG = "HomeViewModel";
    private String userId;
    private final IncomeRepository incomeRepository = new IncomeRepository();
    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final UserStatsRepository userStatsRepository = new UserStatsRepository();
    private final MutableLiveData<List<TransactionItem>> transactions = new MutableLiveData<>();
    private final MutableLiveData<String> totalBalance = new MutableLiveData<>();
    private final MutableLiveData<String> totalExpense = new MutableLiveData<>();
    private String selectedPeriod = "daily";

    public void setUserId(String userId) {
        Log.d(TAG, "setUserId: " + userId);
        this.userId = userId;
        loadData();
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

    public void setSelectedPeriod(String period) {
        Log.d(TAG, "setSelectedPeriod: " + period);
        this.selectedPeriod = period;
        loadData();
    }

    private void loadData() {
        if (userId == null) {
            Log.e(TAG, "userId is null, cannot load data");
            return;
        }

        // Load total balance and expense from userStats
        userStatsRepository.getUserStats(userId).observeForever(userStats -> {
            Log.d(TAG, "userStats received: " + (userStats != null));
            if (userStats != null) {
                long balance = userStats.getTotalIncome() + userStats.getTotalExpense();
                totalBalance.setValue(formatCurrency(balance));
                totalExpense.setValue(formatCurrency(userStats.getTotalExpense()));
            } else {
                totalBalance.setValue("0");
                totalExpense.setValue("0");
            }
        });

        // Load transactions based on selected period
        long startDate = getStartDateForPeriod();
        long endDate = new Date().getTime();
        Log.d(TAG, "Loading data for period: " + selectedPeriod + ", startDate: " + new Date(startDate) + ", endDate: " + new Date(endDate));
        LiveData<List<Income>> incomes = incomeRepository.getIncomesByUserAndDateRange(userId, startDate, endDate);
        LiveData<List<Expense>> expenses = expenseRepository.getExpensesByUserAndDateRange(userId, startDate, endDate);

        incomes.observeForever(incomeList -> {
            List<TransactionItem> transactionList = new ArrayList<>();
            Log.d(TAG, "Incomes received, size: " + (incomeList != null ? incomeList.size() : 0));
            if (incomeList != null) {
                for (Income income : incomeList) {
                    transactionList.add(new TransactionItem(income.getCategory(), income.getDate(), income.getTitle(), income.getAmount()));
                }
            }
            expenses.observeForever(expenseList -> {
                Log.d(TAG, "Expenses received, size: " + (expenseList != null ? expenseList.size() : 0));
                if (expenseList != null) {
                    for (Expense expense : expenseList) {
                        transactionList.add(new TransactionItem(expense.getCategory(), expense.getDate(), expense.getTitle(), expense.getAmount()));
                    }
                }
                // Sort by date descending
                Collections.sort(transactionList, (item1, item2) -> item2.getDate().compareTo(item1.getDate()));
                transactions.setValue(transactionList);
            });
        });
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
        private String category;
        private Timestamp date;
        private String title;
        private long amount;

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