package com.example.stackoverflow.data.repository.getquestionsrepository

import androidx.paging.PagingData
import com.example.stackoverflow.data.room.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow
import com.example.stackoverflow.domain.utils.Result

interface QuestionRepository {
    fun getQuestions(): Flow<PagingData<QuestionEntity>>

    fun getQuestionsErrorFlow(): Flow<Result<String>>

    suspend fun loadQuestions()
}