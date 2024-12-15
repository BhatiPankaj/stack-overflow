package com.example.stackoverflow.presentation.questionsearchscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.usecase.SearchUseCase
import com.example.stackoverflow.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionSearchViewModel @Inject constructor(private val searchUseCase: SearchUseCase) :
    ViewModel() {

    var lastResult: Result<String>? = null
    val questionsErrorFlow = searchUseCase.questionsErrorFlow
    val searchedHistoryList: Flow<List<SearchHistoryEntity>?> = searchUseCase.getSearchedHistory()

    private val searchQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult: Flow<PagingData<QuestionEntity>> = searchQuery.flatMapLatest { query ->
        if(query.isBlank()) {
            flowOf(PagingData.empty())
        } else {
            searchUseCase.searchQuestion(query)
        }
    }.cachedIn(viewModelScope)

    fun search(query: String) {
        searchQuery.value = query
    }

    fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity) {
        viewModelScope.launch {
            searchUseCase.deletedSearchedHistory(searchHistoryEntity)
        }
    }
}