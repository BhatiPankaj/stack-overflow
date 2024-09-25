package com.example.stackoverflow.presentation.util

import androidx.compose.runtime.saveable.Saver
import com.example.stackoverflow.domain.utils.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Custom Saver for the Result class
fun <T> resultSaver(dataClassType: Class<T>): Saver<Result<T>, String> {
    return Saver(
        save = { result ->
            // Convert the Result to a JSON String for saving
            when (result) {
                is Result.Loading -> "Loading"
                is Result.Empty -> "Empty"
                is Result.Success -> Gson().toJson(result.data) // Serialize only the success data
                is Result.Failure -> Gson().toJson(
                    mapOf(
                        "errorMessage" to result.errorMessage,
                        "exceptionType" to result.exceptionType,
                        "exception" to result.exception?.message
                    )
                ) // Serialize the failure details
            }
        },
        restore = { savedString ->
            // Restore the Result from the saved JSON String
            when (savedString) {
                "Loading" -> Result.Loading
                "Empty" -> Result.Empty
                else -> {
                    try {
                        // Try to deserialize it as Success data
                        val successData: T = Gson().fromJson(savedString, dataClassType)
                        Result.Success(successData)
                    } catch (e: Exception) {
                        // If deserialization fails, assume it's a Failure
                        val failureData = Gson().fromJson<Map<String, String>>(savedString, object : TypeToken<Map<String, String>>() {}.type)
                        Result.Failure(
                            errorMessage = failureData["errorMessage"] ?: "Unknown error",
                            exceptionType = Result.ErrorType.valueOf(failureData["exceptionType"] ?: "OTHER"),
                            exception = failureData["exception"]?.let { Exception(it) }
                        )
                    }
                }
            }
        }
    )
}
