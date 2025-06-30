package com.example.financewise.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financewise.data.model.Expense;
import com.example.financewise.data.model.Income;
import com.example.financewise.data.model.UserStats;
import com.example.financewise.data.repository.ExpenseRepository;
import com.example.financewise.data.repository.IncomeRepository;
import com.example.financewise.data.repository.UserRepository;
import com.example.financewise.data.repository.UserStatsRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddTransactionViewModel extends BaseViewModel{
    private static final String TAG = "AddTransactionViewModel";
    private String userId;
    private IncomeRepository incomeRepository;
    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private UserStatsRepository userStatsRepository;

    private final MutableLiveData<Boolean> transactionResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AddTransactionViewModel(){

    }
    public void setUserId(String userId){
        if(this.userId == null || !this.equals(userId)){
            this.userId = userId;
            initializeRepositories();
        }
    }
    private void initializeRepositories(){
        if(userId != null){
            incomeRepository = new IncomeRepository(userId);
            expenseRepository = new ExpenseRepository(userId);
            userRepository = new UserRepository(userId);
            userStatsRepository = new UserStatsRepository(userId);
            Log.d(TAG, "Repositories initialized for userId: " + userId);
        }else{
            Log.e(TAG, "Cannot initialize repositories, userId is null");
        }
    }

    public LiveData<Boolean> getTransactionResult() {
        return transactionResult;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public void addIncome(Income income){
        if(userId == null){
            transactionResult.setValue(false);
            return;
        }
        isLoading.setValue(true);
        incomeRepository.addIncome(income, task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "Income added successfully");
                updateUserStatsAndBalance(income.getAmount(), true, income.getDate().toDate());
            }else{
                Log.e(TAG, "Failed to add income: " + task.getException());
                transactionResult.setValue(false);
                isLoading.setValue(false);
            }
        });
    }
    public void addExpense(Expense expense){
        if(userId == null){
            transactionResult.setValue(false);
            return;
        }
        isLoading.setValue(true);
        expenseRepository.addExpense(expense, task ->{
            if (task.isSuccessful()){
                Log.d(TAG, "Expense added successfully");
                updateUserStatsAndBalance(expense.getAmount(), false, expense.getDate().toDate());
            } else {
                Log.e(TAG, "Failed to add expense: " + task.getException());
                transactionResult.setValue(false);
                isLoading.setValue(false);
            }
        });
    }
    private void updateUserStatsAndBalance(Long amount, boolean isIncome, Date date){
        userStatsRepository.getUserStatsDocumentRef().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.exists()){
                    Log.d(TAG, "User stats document found, updating balance");
                    updateExistingUserStats(amount, isIncome, date);
                }else{
                    Log.d(TAG, "User stats document not found, creating new stats");
                    createNewUserStats(amount, isIncome, date);
                }
            }else {
                Log.e(TAG, "Failed to get user stats document: " + task.getException());
                transactionResult.setValue(false);
                isLoading.setValue(false);
            }
        });
        userRepository.getUserDocumentRef(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "totalBalance updated in User collection");
            }else {
                Log.e(TAG, "Failed to update totalBalance in User collection: " + task.getException());
            }
        });
    }
    private void updateExistingUserStats(Long amount, boolean isIncome, Date date){
        long absAmount = amount;
        String totalField = isIncome ? "totalIncome" : "totalExpense";
        userStatsRepository.getUserStatsDocumentRef().update(totalField, FieldValue.increment(absAmount))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "Updated " + totalField);
                        updateDailyWeeklyMonthlyStats(amount, isIncome, date);
                    }else {
                        Log.e(TAG, "Failed to update " + totalField + ": " + task.getException());
                        transactionResult.setValue(false);
                        isLoading.setValue(false);
                    }
                });
    }

    private void createNewUserStats(long transactionAmount, boolean isIncome, Date transactionDate) {
        UserStats newUserStats = new UserStats();
        long absAmount = Math.abs(transactionAmount);

        if (isIncome) {
            newUserStats.setTotalIncome(absAmount);
            newUserStats.setTotalExpense(0);
            newUserStats.setIncomeToday(isToday(transactionDate) ? absAmount : 0);
            newUserStats.setExpenseToday(0);
            newUserStats.setIncomeThisWeek(isThisWeek(transactionDate) ? absAmount : 0);
            newUserStats.setExpenseThisWeek(0);
            newUserStats.setIncomeThisMonth(isThisMonth(transactionDate) ? absAmount : 0);
            newUserStats.setExpenseThisMonth(0);
        } else {
            newUserStats.setTotalIncome(0);
            newUserStats.setTotalExpense(absAmount);
            newUserStats.setIncomeToday(0);
            newUserStats.setExpenseToday(isToday(transactionDate) ? absAmount : 0);
            newUserStats.setIncomeThisWeek(0);
            newUserStats.setExpenseThisWeek(isThisWeek(transactionDate) ? absAmount : 0);
            newUserStats.setIncomeThisMonth(0);
            newUserStats.setExpenseThisMonth(isThisMonth(transactionDate) ? absAmount : 0);
        }

        userStatsRepository.getUserStatsDocumentRef().set(newUserStats)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "New UserStats document created successfully.");
                        transactionResult.setValue(true);
                    } else {
                        Log.e(TAG, "Failed to create new UserStats document: " + task.getException());
                        transactionResult.setValue(false);
                    }
                    isLoading.setValue(false);
                });
    }
    private void updateDailyWeeklyMonthlyStats(long amount, boolean isIncome, Date date){
        String todayField = isIncome ? "incomeToday" : "expenseToday";
        String weekField = isIncome ? "incomeThisWeek" : "expenseThisWeek";
        String monthField = isIncome ? "incomeThisMonth" : "expenseThisMonth";

        userStatsRepository.getUserStatsDocumentRef().get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()){
                UserStats currentStats = task.getResult().toObject(UserStats.class);
                if(currentStats == null){
                    transactionResult.setValue(false);
                    isLoading.setValue(false);
                    return;
                }

                AtomicBoolean fieldsToUpdate = new AtomicBoolean(false);

                Map<String, Object> updates = new HashMap<>();

                if(isToday(date)){
                    updates.put(todayField, FieldValue.increment(amount));
                    fieldsToUpdate.set(true);
                }

                if(isThisWeek(date)){
                    updates.put(weekField, FieldValue.increment(amount));
                    fieldsToUpdate.set(true);
                }
                if(isThisMonth(date)){
                    updates.put(monthField, FieldValue.increment(amount));
                    fieldsToUpdate.set(true);
                }
                if(fieldsToUpdate.get()){
                    userStatsRepository.getUserStatsDocumentRef().update(updates)
                            .addOnCompleteListener(updateTask ->{
                                if(updateTask.isSuccessful()){
                                    Log.d(TAG, "Updated daily, weekly, and monthly stats successfully.");
                                    transactionResult.setValue(true);
                                } else {
                                    Log.e(TAG, "Failed to update daily, weekly, and monthly stats: " + updateTask.getException());
                                    transactionResult.setValue(false);
                                }
                                isLoading.setValue(false);
                            });
                }else {
                    Log.d(TAG, "No daily/weekly/monthly stats update needed for this transaction date");
                    transactionResult.setValue(true);
                    isLoading.setValue(false);
                }
            }else {
                Log.e(TAG, "Failed to get current UserStats for daily/weekly/monthly update: " + task.getException());
                transactionResult.setValue(false);
                isLoading.setValue(false);
            }
        });
    }
    private boolean isToday(Date date){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    private boolean isThisWeek(Date date){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        return cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    private boolean isThisMonth(Date date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date);
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
    @Override
    protected void onCleared(){
        super.onCleared();
    }
}
