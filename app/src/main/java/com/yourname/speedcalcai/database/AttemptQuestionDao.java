package com.yourname.speedcalcai.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.yourname.speedcalcai.models.AttemptQuestion;
import java.util.List;

@Dao
public interface AttemptQuestionDao {
    @Insert
    void insertAll(List<AttemptQuestion> questions);

    @Query("SELECT * FROM attempt_questions WHERE attemptId = :attemptId")
    List<AttemptQuestion> getQuestionsForAttempt(int attemptId);

    @Query("SELECT * FROM attempt_questions WHERE status = 'WRONG' ORDER BY id DESC")
    List<AttemptQuestion> getWrongQuestions();
}
