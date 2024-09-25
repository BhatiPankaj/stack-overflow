package com.example.stackoverflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.data.room.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StackOverflowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(question: List<QuestionEntity>)

    @Query("Select * from questions")
    fun getQuestions(): Flow<List<QuestionEntity>>

    @Query("Select * from answers where questionId = :questionId")
    fun getAnswers(questionId: Long): Flow<List<AnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)
}