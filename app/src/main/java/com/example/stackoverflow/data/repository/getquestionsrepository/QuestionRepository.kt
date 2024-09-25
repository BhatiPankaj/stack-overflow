package com.example.stackoverflow.data.repository.getquestionsrepository

import com.example.stackoverflow.data.room.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow
import com.example.stackoverflow.domain.utils.Result

interface QuestionRepository {
    fun getQuestions(): Flow<List<QuestionEntity>>

    fun getQuestionsErrorFlow(): Flow<Result<String>>

    suspend fun loadQuestions()
}