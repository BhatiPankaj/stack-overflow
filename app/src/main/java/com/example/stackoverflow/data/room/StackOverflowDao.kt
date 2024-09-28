package com.example.stackoverflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StackOverflowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(question: List<QuestionEntity>)

    @Query("Select * from questions")
    fun getQuestions(): Flow<List<QuestionEntity>>

    @Query("Select * from answers where questionId = :questionId Order By score Desc")
    fun getAnswers(questionId: Long): Flow<List<AnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchedText(searchedText: SearchHistoryEntity)

    @Query("Select * from search_history Order BY id Desc limit :limit ")
    fun getSearchedHistory(limit: Int): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM SEARCH_HISTORY WHERE id=:id")
    suspend fun deletedSearchedEntry(id: Int)

}