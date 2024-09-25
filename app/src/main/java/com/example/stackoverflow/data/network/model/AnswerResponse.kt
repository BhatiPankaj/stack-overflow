package com.example.stackoverflow.data.network.model

import com.google.gson.annotations.SerializedName

data class AnswerResponse(
    @SerializedName("question_id") val questionId: Long,
    @SerializedName("answer_id") val answerId: Long,
    @SerializedName("is_accepted") val isAccepted: Boolean,
    val body: String?,
    val score: Int,
    val owner: Owner
)
