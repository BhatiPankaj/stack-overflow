package com.example.stackoverflow.data.network

import com.example.stackoverflow.data.network.model.AnswersResponse
import com.example.stackoverflow.data.network.model.QuestionsResponse
import com.example.stackoverflow.data.network.utils.Order
import com.example.stackoverflow.data.network.utils.SearchSort
import com.example.stackoverflow.data.network.utils.Sort
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val API_BASE_URL = "https://api.stackexchange.com/2.3/"
const val API_KEY = "rl_pAk55PExTsFX1akAEocMg6za4"

interface StackOverflowNetworkService {
    @GET("questions")
    suspend fun getQuestions(
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int,
        @Query("order") order: Order,
        @Query("sort") sort: Sort,
        @Query("key") key: String = API_KEY,
        @Query("site") site: String = "stackoverflow"
    ): Response<QuestionsResponse>

    @GET("questions/{id}/answers")
    suspend fun getAnswers(
        @Path("id") questionId: Long,
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int,
        @Query("order") order: Order,
        @Query("sort") sort: Sort,
        @Query("key") key: String = API_KEY,
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody"
    ): Response<AnswersResponse>

    @GET("similar")
    suspend fun search(
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int,
        @Query("title") query: String,
        @Query("order") order: Order,
        @Query("sort") sort: SearchSort,
        @Query("key") key: String = API_KEY,
        @Query("site") site: String = "stackoverflow"
    ): Response<QuestionsResponse>
}