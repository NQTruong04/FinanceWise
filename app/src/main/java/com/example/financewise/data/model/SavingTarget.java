package com.example.financewise.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SavingTarget {
    private String id;
    private String userId;
    private long goal;
    private long amountSaved;
    private String category;
    @ServerTimestamp
    private Timestamp createdAt;

    public SavingTarget() {
        // Default constructor required for Firestore
    }

    public SavingTarget(String userId, long goal, long amountSaved, String category) {
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
    public long getGoal() { return goal; }
    public void setGoal(long goal) { this.goal = goal; }
    public long getAmountSaved() { return amountSaved; }
    public void setAmountSaved(long amountSaved) { this.amountSaved = amountSaved; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
