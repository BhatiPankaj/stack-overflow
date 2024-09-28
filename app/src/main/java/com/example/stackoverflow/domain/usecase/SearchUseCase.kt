package com.example.stackoverflow.domain.usecase

import com.example.stackoverflow.data.repository.searchquestionrepository.SearchQuestionRepository
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import javax.inject.Inject


class SearchUseCase @Inject constructor(private val searchRepository: SearchQuestionRepository) {
    suspend fun searchQuestion(query: String) = searchRepository.searchQuestion(query)

    val questionsErrorFlow = searchRepository.getSearchErrorFlow()

    fun getSearchedHistory() = searchRepository.getSearchedHistory()

    suspend fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity) = searchRepository.deletedSearchedHistory(searchHistoryEntity)
}