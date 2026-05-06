package com.yourname.speedcalcai.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.yourname.speedcalcai.models.Attempt;
import java.util.List;

@Dao
public interface AttemptDao {
    @Insert
    long insert(Attempt attempt);

    @Query("SELECT * FROM attempts ORDER BY createdAt DESC")
    List<Attempt> getAllAttempts();

    @Query("SELECT * FROM attempts WHERE moduleName = :module ORDER BY createdAt DESC")
    List<Attempt> getAttemptsByModule(String module);

    @Query("SELECT * FROM attempts WHERE attemptId = :id LIMIT 1")
    Attempt getAttemptById(int id);
}
