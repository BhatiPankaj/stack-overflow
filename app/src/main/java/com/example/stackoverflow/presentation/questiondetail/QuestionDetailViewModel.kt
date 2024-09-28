package com.example.stackoverflow.presentation.questiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.domain.usecase.GetAnswersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(private val useCase: GetAnswersUseCase) :
    ViewModel() {

    fun getErrorFlow() = useCase.getErrorFlow()

    fun getAnswers(questionId: Long) = useCase.getAnswers(questionId)

    fun fetchAnswers(questionId: Long) {
        viewModelScope.launch {
            useCase.fetchAnswers(questionId)
        }
    }
}