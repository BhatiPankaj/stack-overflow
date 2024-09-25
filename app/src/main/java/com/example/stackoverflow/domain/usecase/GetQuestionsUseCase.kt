package com.example.stackoverflow.domain.usecase

import com.example.stackoverflow.data.repository.getquestionsrepository.QuestionRepository
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(private val questionRepository: QuestionRepository) {
    fun getQuestions() = questionRepository.getQuestions()

    val questionsErrorFlow = questionRepository.getQuestionsErrorFlow()

    suspend operator fun invoke() {
        questionRepository.loadQuestions()
    }
}