package com.example.financewise.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SavingTarget {
    private String id;
    private String userId;
    private double goal;
    private double amountSaved;
    private String category;
    @ServerTimestamp
    private Date createdAt;

    public SavingTarget() {
        // Default constructor required for Firestore
    }

    public SavingTarget(String userId, double goal, double amountSaved, String category) {
        this.userId = userId;
        this.goal = goal;
        this.amountSaved = amountSaved;
        this.category = category;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getGoal() { return goal; }
    public void setGoal(double goal) { this.goal = goal; }
    public double getAmountSaved() { return amountSaved; }
    public void setAmountSaved(double amountSaved) { this.amountSaved = amountSaved; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
