package com.example.stackoverflow.data.repository.getquestionsrepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val networkService: StackOverflowNetworkService,
    private val database: AppDatabase
) : QuestionRepository {
    private val questionsErrorFlow = MutableSharedFlow<Result<String>>()

    @OptIn(ExperimentalPagingApi::class)
    override fun getQuestions(): Flow<PagingData<QuestionEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = QuestionRemoteMediator(networkService, database, questionsErrorFlow),
            pagingSourceFactory = {
                database.stackOverflowDao().getQuestions()
            }
        ).flow
    }

    override fun getQuestionsErrorFlow(): Flow<Result<String>> = questionsErrorFlow

    override suspend fun loadQuestions() {

    }
}