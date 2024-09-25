package com.example.stackoverflow.data.repository.getquestionsrepository

import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.network.model.QuestionResponse
import com.example.stackoverflow.data.network.utils.Order
import com.example.stackoverflow.data.network.utils.ServiceProvider
import com.example.stackoverflow.data.network.utils.Sort
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.domain.utils.DataMapper
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

    override fun getQuestions(): Flow<List<QuestionEntity>> =
        database.stackOverflowDao().getQuestions()

    override fun getQuestionsErrorFlow(): Flow<Result<String>> = questionsErrorFlow

    override suspend fun loadQuestions() {
        questionsErrorFlow.emit(Result.Loading)
        val result = ServiceProvider.makeNetworkSafeCall {
            networkService.getQuestions(
                pageSize = 30,
                order = Order.asc,
                sort = Sort.activity
            )
        }

        when (result) {
            is Result.Empty -> questionsErrorFlow.emit(result)
            is Result.Failure -> questionsErrorFlow.emit(result)
            is Result.Success ->  {
                insertQuestions(result.data.items)
                questionsErrorFlow.emit(Result.Success("Updated questions successfully fetched from server"))
            }
            is Result.Loading -> questionsErrorFlow.emit(result)
        }
    }

    private suspend fun insertQuestions(questionResponseList: List<QuestionResponse>) {
        database.stackOverflowDao().insertQuestions(questionResponseList.map {
            DataMapper.toEntity(it)
        })
    }
}