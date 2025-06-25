package com.example.financewise.data.model;

public class UserStats {
    private long incomeToday, expenseToday, incomeThisWeek, expenseThisWeek;
    private long incomeThisMonth, expenseThisMonth, totalIncome, totalExpense;

    public UserStats() {}

    // Getters and setters
    public long getIncomeToday() { return incomeToday; }
    public void setIncomeToday(long incomeToday) { this.incomeToday = incomeToday; }
    public long getExpenseToday() { return expenseToday; }
    public void setExpenseToday(long expenseToday) { this.expenseToday = expenseToday; }
    public long getIncomeThisWeek() { return incomeThisWeek; }
    public void setIncomeThisWeek(long incomeThisWeek) { this.incomeThisWeek = incomeThisWeek; }
    public long getExpenseThisWeek() { return expenseThisWeek; }
    public void setExpenseThisWeek(long expenseThisWeek) { this.expenseThisWeek = expenseThisWeek; }
    public long getIncomeThisMonth() { return incomeThisMonth; }
    public void setIncomeThisMonth(long incomeThisMonth) { this.incomeThisMonth = incomeThisMonth; }
    public long getExpenseThisMonth() { return expenseThisMonth; }
    public void setExpenseThisMonth(long expenseThisMonth) { this.expenseThisMonth = expenseThisMonth; }
    public long getTotalIncome() { return totalIncome; }
    public void setTotalIncome(long totalIncome) { this.totalIncome = totalIncome; }
    public long getTotalExpense() { return totalExpense; }
    public void setTotalExpense(long totalExpense) { this.totalExpense = totalExpense; }
}