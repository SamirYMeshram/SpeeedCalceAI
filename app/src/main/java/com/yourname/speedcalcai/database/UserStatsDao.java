package com.yourname.speedcalcai.database;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface UserStatsDao {
    @Query("SELECT COUNT(*) FROM attempts")
    int attemptCount();
}
