package com.example.stackoverflow.data.repository.searchquestionrepository

import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface SearchQuestionRepository {
    suspend fun searchQuestion(query: String): Flow<List<QuestionEntity>>

    fun getSearchErrorFlow(): Flow<Result<String>>

    fun getSearchedHistory(): Flow<List<SearchHistoryEntity>>

    suspend fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity)
}