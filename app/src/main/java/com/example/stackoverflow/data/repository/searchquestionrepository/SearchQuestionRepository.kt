package com.example.stackoverflow.data.repository.searchquestionrepository

import androidx.paging.PagingData
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface SearchQuestionRepository {
    fun searchQuestion(query: String): Flow<PagingData<QuestionEntity>>

    fun getSearchErrorFlow(): Flow<Result<String>>

    fun getSearchedHistory(): Flow<List<SearchHistoryEntity>>

    suspend fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity)
}