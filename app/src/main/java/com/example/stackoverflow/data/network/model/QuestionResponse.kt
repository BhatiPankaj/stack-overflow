package com.example.stackoverflow.data.network.model

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("question_id") val id: Long,
    val title: String,
    @SerializedName("link") val questionLink: String,
    val tags: List<String>,
    val owner: Owner
)
