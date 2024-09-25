package com.example.stackoverflow.presentation.questionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.domain.usecase.GetQuestionsUseCase
import com.example.stackoverflow.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor(private val getQuestionsUseCase: GetQuestionsUseCase): ViewModel() {
    init {
        viewModelScope.launch {
            getQuestionsUseCase.invoke()
        }
    }

    val questionListFlow = getQuestionsUseCase.getQuestions()
    val questionsErrorFlow = getQuestionsUseCase.questionsErrorFlow
    var lastResult: Result<*> = Result.Loading
}