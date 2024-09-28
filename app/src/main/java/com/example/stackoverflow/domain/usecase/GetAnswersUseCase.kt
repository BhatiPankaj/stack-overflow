package com.example.stackoverflow.domain.usecase

import com.example.stackoverflow.data.repository.getanswerssrepository.AnswerRepository
import javax.inject.Inject

class GetAnswersUseCase @Inject constructor(private val answerRepository: AnswerRepository) {
    fun getAnswers(questionId: Long) = answerRepository.getAnswers(questionId)

    fun getErrorFlow() = answerRepository.getAnswersErrorFlow()

    suspend fun fetchAnswers(questionId: Long) = answerRepository.fetchAnswers(questionId)
}