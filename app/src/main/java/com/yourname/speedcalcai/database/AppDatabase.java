package com.yourname.speedcalcai.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.yourname.speedcalcai.models.Attempt;
import com.yourname.speedcalcai.models.AttemptQuestion;

@Database(entities = {Attempt.class, AttemptQuestion.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract AttemptDao attemptDao();
    public abstract AttemptQuestionDao attemptQuestionDao();
    public abstract UserStatsDao userStatsDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "speedcalc_ai.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
