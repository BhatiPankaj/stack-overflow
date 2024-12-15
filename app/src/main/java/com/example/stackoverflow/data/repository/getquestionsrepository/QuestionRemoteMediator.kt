package com.example.stackoverflow.data.repository.getquestionsrepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.network.model.QuestionResponse
import com.example.stackoverflow.data.network.utils.Order
import com.example.stackoverflow.data.network.utils.ServiceProvider
import com.example.stackoverflow.data.network.utils.Sort
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.RemoteKeyEntity
import com.example.stackoverflow.domain.utils.DataMapper
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalPagingApi::class)
class QuestionRemoteMediator(
    private val networkService: StackOverflowNetworkService,
    private val database: AppDatabase,
    private val questionsErrorFlow: MutableSharedFlow<Result<String>>,
    private val initialPage: Int = 1
) : RemoteMediator<Int, QuestionEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, QuestionEntity>
    ): MediatorResult {
        return try {
            val page: Int = when (loadType) {
                LoadType.REFRESH -> initialPage // Initial Load
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // No prepend in this case
                LoadType.APPEND -> {
                    database.remoteKeyDao().getRemoteKey()?.nextPage ?: initialPage
                }
            }

//         questionsErrorFlow.emit(Result.Loading)
            val result = ServiceProvider.makeNetworkSafeCall {
                networkService.getQuestions(
                    page = page,
                    pageSize = state.config.pageSize,
                    order = Order.asc,
                    sort = Sort.activity
                )
            }
            println("Page: $page")
            val endOfPaginationReached: Boolean = when (result) {
                is Result.Empty -> {
                    println("Empty list")
                    questionsErrorFlow.emit(result)
                    database.remoteKeyDao().insertRemoteKey(RemoteKeyEntity(nextPage = null))
                    true
                }

                is Result.Failure -> {
                    questionsErrorFlow.emit(result)
                    return MediatorResult.Error(result.exception!!)
                }

                is Result.Success -> {
                    println(result.data.items.toString())
                    database.withTransaction {
                        if(loadType == LoadType.REFRESH) {
                            questionsErrorFlow.emit(Result.Success("Updated questions successfully fetched from server"))
                            database.remoteKeyDao().clearRemoteKeys()
                            database.stackOverflowDao().clearAllItems()
                        }
                        database.remoteKeyDao().insertRemoteKey(RemoteKeyEntity(nextPage = page + 1))
                    }
                    insertQuestions(result.data.items)
                    false
                }

                is Result.Loading -> {
                    questionsErrorFlow.emit(result)
                    false
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun insertQuestions(questionResponseList: List<QuestionResponse>) {
        database.stackOverflowDao().insertQuestions(questionResponseList.map {
            DataMapper.toEntity(it)
        })
    }
}
