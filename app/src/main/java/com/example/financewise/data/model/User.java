package com.example.financewise.data.model;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private long totalBalance;
    @ServerTimestamp
    private Timestamp createdAt;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String userId, String name, String email, String phone, long totalBalance) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.totalBalance = totalBalance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
