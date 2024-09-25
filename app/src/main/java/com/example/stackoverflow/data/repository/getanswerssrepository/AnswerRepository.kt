package com.example.stackoverflow.data.repository.getanswerssrepository

import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    fun getAnswers(questionId: Long): Flow<List<AnswerEntity>>

    fun getAnswersErrorFlow(): Flow<Result<String>>

    suspend fun fetchAnswers(questionId: Long)
}