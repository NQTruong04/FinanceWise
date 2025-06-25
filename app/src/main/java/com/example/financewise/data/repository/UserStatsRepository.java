package com.example.financewise.data.repository;

import androidx.lifecycle.LiveData;

import com.example.financewise.data.model.UserStats;

public class UserStatsRepository extends BaseFirestoreRepository<UserStats>{

    public UserStatsRepository() {
        super("userStats");
    }

    public LiveData<UserStats> getUserStats(String userId) {
        return getItem(userId, UserStats.class);
    }
}
