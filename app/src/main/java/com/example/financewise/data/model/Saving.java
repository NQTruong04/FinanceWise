package com.example.financewise.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Saving {
    private String id;
    private Date date;
    private String category;
    private double amount;
    private String title;
    private String message;
    @ServerTimestamp
    private Date createdAt;

    public Saving() {
        // Default constructor required for Firestore
    }

    public Saving(Date date, String category, double amount, String title, String message) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.title = title;
        this.message = message;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
