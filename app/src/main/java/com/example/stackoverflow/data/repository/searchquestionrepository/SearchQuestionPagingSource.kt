package com.example.stackoverflow.data.repository.searchquestionrepository

import androidx.paging.PagingSource
import androidx.paging.PagingState
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
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.Locale

class SearchQuestionPagingSource(
    private val networkService: StackOverflowNetworkService,
    private val database: AppDatabase,
    private val searchingErrorFlow: MutableSharedFlow<Result<String>>,
    private val initialPage: Int = 1,
    private val query: String
) : PagingSource<Int, QuestionEntity>() {

    override fun getRefreshKey(state: PagingState<Int, QuestionEntity>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestionEntity> {
        return try {
            // Start the load at page 1
            val page = params.key ?: 1
            println("Search Page: $page")
            if(page == 1) {
                searchingErrorFlow.emit(Result.Loading)
            }
            val result = ServiceProvider.makeNetworkSafeCall {
                networkService.search(
                    page = page,
                    pageSize = 20,
                    order = Order.asc,
                    sort = SearchSort.relevance,
                    query = query
                )
            }

            val data = mutableListOf<QuestionEntity>()

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
                                ),
                                nextKey = page + 1
                            )
                        )
                        data.addAll(getEntityData(result.data.items))
                        searchingErrorFlow.emit(Result.Success("Updated questions successfully fetched from server"))
                    }
                }
                is Result.Loading -> searchingErrorFlow.emit(result)
            }

            return LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,  // No previous page if we're at the first page
                nextKey = if (data.isEmpty()) null else page + 1  // No next page if response is empty
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun getEntityData(questionResponse: List<QuestionResponse>): List<QuestionEntity> {
        return questionResponse.map { DataMapper.toEntity(it) }
    }
}