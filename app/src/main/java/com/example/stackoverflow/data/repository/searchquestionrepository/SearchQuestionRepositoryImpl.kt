package com.example.stackoverflow.data.repository.searchquestionrepository

import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.network.model.QuestionResponse
import com.example.stackoverflow.data.network.utils.Order
import com.example.stackoverflow.data.network.utils.SearchSort
import com.example.stackoverflow.data.network.utils.ServiceProvider
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.utils.DataMapper
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchQuestionRepositoryImpl @Inject constructor(private val networkService: StackOverflowNetworkService,
                                   private val database: AppDatabase
): SearchQuestionRepository {
    private val searchingErrorFlow = MutableSharedFlow<Result<String>>()

    override suspend fun searchQuestion(query: String): Flow<List<QuestionEntity>> {
        searchingErrorFlow.emit(Result.Loading)
        return flow {
            val result = ServiceProvider.makeNetworkSafeCall {
                networkService.search(
                    pageSize = 30,
                    order = Order.asc,
                    sort = SearchSort.relevance,
                    query = query
                )
            }

            when (result) {
                is Result.Empty -> searchingErrorFlow.emit(result)
                is Result.Failure -> searchingErrorFlow.emit(result)
                is Result.Success ->  {
                    if(result.data.items.isEmpty()) {
                        searchingErrorFlow.emit(Result.Empty)
                    } else {
                        database.stackOverflowDao().insertSearchedText(
                            SearchHistoryEntity(
                                searchedText = query.lowercase(
                                    Locale.ROOT
                                )
                            ))
                        searchingErrorFlow.emit(Result.Success("Updated questions successfully fetched from server"))
                        emit(getEntityData(result.data.items))
                    }
                }
                is Result.Loading -> searchingErrorFlow.emit(result)
            }
        }
    }

    private fun getEntityData(questionResponse: List<QuestionResponse>): List<QuestionEntity> {
        return questionResponse.map { DataMapper.toEntity(it) }
    }

    override fun getSearchErrorFlow(): Flow<Result<String>> = searchingErrorFlow

    override fun getSearchedHistory(): Flow<List<SearchHistoryEntity>> {
        return database.stackOverflowDao().getSearchedHistory(10)
    }

    override suspend fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity) {
        database.stackOverflowDao().deletedSearchedEntry(searchHistoryEntity.id)
    }
}