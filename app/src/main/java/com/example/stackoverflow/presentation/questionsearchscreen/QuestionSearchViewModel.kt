package com.example.stackoverflow.presentation.questionsearchscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.usecase.SearchUseCase
import com.example.stackoverflow.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionSearchViewModel @Inject constructor(private val searchUseCase: SearchUseCase) :
    ViewModel() {

    var lastResult: Result<String>? = null
    val questionsErrorFlow = searchUseCase.questionsErrorFlow
    val searchedHistoryList: Flow<List<SearchHistoryEntity>?> = searchUseCase.getSearchedHistory()

    private val _searchedList = MutableStateFlow<List<QuestionEntity>?>(null)
    val searchedList: StateFlow<List<QuestionEntity>?> = _searchedList

    fun search(query: String) {
        viewModelScope.launch {
            searchUseCase.searchQuestion(query).collect {
                _searchedList.value = it
            }
        }
    }

    fun deletedSearchedHistory(searchHistoryEntity: SearchHistoryEntity) {
        viewModelScope.launch {
            searchUseCase.deletedSearchedHistory(searchHistoryEntity)
        }
    }
}