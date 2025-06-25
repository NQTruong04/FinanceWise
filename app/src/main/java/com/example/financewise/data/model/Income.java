package com.example.financewise.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Income {
    private String id;
    private String userId;
    private String category;
    private Timestamp date;
    private long amount;
    private String title;
    private String message;
    @ServerTimestamp
    private Timestamp createdAt;

    public Income() {
        // Default constructor required for Firestore
    }

    public Income(String userId, String category, Timestamp date, long amount, String title, String message) {
        this.userId = userId;
        this.category = category;
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.message = message;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
