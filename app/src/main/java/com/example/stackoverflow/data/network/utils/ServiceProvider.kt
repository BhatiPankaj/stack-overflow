package com.example.stackoverflow.data.network.utils
import com.example.stackoverflow.data.network.NoInternetException
import com.example.stackoverflow.domain.utils.Result
import retrofit2.Response

object ServiceProvider {
    suspend fun <T> makeNetworkSafeCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            return if (response.isSuccessful)
                if (response.body() == null || response.body().toString().isEmpty())
                    Result.Empty
                else
                    Result.Success(response.body()!! as T)
            else
                Result.Failure("Getting error while making the network API call.. The error code is ${response.code()}", Result.ErrorType.OTHER)
        } catch (ex: NoInternetException) {
            Result.Failure("No internet connection.", Result.ErrorType.NETWORK_ISSUE, ex)
        }
        catch (ex: Exception) {
            Result.Failure(ex.message ?: "Getting unknown exception while making the network API call.", Result.ErrorType.OTHER, ex)
        }
    }
}