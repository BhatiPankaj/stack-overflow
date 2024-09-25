package com.example.stackoverflow.domain.utils

import java.lang.Exception

sealed class Result<out T> {
    enum class ErrorType {
        NETWORK_ISSUE, OTHER
    }

    data object Loading : Result<Nothing>()
    data object Empty : Result<Nothing>()
    data class Success<out T>(val data: T): Result<T>()
    data class Failure(val errorMessage: String, val exceptionType: ErrorType, val exception: Exception? = null): Result<Nothing>()
}