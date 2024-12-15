package com.example.stackoverflow.data.repository.searchquestionrepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchQuestionRepositoryImpl @Inject constructor(
    private val networkService: StackOverflowNetworkService,
    private val database: AppDatabase
) : SearchQuestionRepository {
    private val searchingErrorFlow = MutableSharedFlow<Result<String>>()

    override fun searchQuestion(query: String): Flow<PagingData<QuestionEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            )
        ) {
            SearchQuestionPagingSource(networkService, database, searchingErrorFlow, query = query)
        }.flow
    }

    override fun getSearchErrorFlow(): Flow<Result<String>> = searchingErrorFlow

    override fun getSearchedHistory(): Flow<List<SearchHistoryEntity>> {
        return database.stackOverflowDao().getSearchedHistory(10)
    }

    override suspend fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity) {
        database.stackOverflowDao().deletedSearchedEntry(searchHistoryEntity.id)
    }
}