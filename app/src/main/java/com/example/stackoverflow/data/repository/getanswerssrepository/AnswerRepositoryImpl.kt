package com.example.stackoverflow.data.repository.getanswerssrepository

import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.network.model.AnswerResponse
import com.example.stackoverflow.data.network.utils.Order
import com.example.stackoverflow.data.network.utils.ServiceProvider
import com.example.stackoverflow.data.network.utils.Sort
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.domain.utils.DataMapper
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerRepositoryImpl @Inject constructor(
    private val networkService: StackOverflowNetworkService,
    private val database: AppDatabase
) : AnswerRepository {
    private val answersErrorFlow = MutableSharedFlow<Result<String>>()

    override suspend fun fetchAnswers(questionId: Long) {
        answersErrorFlow.emit(Result.Loading)
        val result = ServiceProvider.makeNetworkSafeCall {
            networkService.getAnswers(
                questionId,
                pageSize = 30,
                order = Order.asc,
                sort = Sort.activity
            )
        }

        when (result) {
            is Result.Empty -> answersErrorFlow.emit(result)
            is Result.Failure -> answersErrorFlow.emit(result)
            is Result.Success -> {
                insertAnswers(result.data.items)
                answersErrorFlow.emit(Result.Success("Answers fetched successfully from server"))
            }

            is Result.Loading -> answersErrorFlow.emit(result)
        }
    }

    private suspend fun insertAnswers(answers: List<AnswerResponse>) {
        database.stackOverflowDao().insertAnswers(
            answers.map { DataMapper.toEntity(it) }
        )
    }

    override fun getAnswersErrorFlow(): Flow<Result<String>> = answersErrorFlow

    override fun getAnswers(questionId: Long): Flow<List<AnswerEntity>> = database.stackOverflowDao().getAnswers(questionId)

}