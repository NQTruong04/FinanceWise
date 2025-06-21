package com.example.financewise.data.model;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private double totalBalance;
    @ServerTimestamp
    private Date createdAt;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String userId, String name, String email, String phone, double totalBalance) {
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

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
