package com.example.stackoverflow.data.network.model

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("account_id") val accountId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("profile_image") val profileImageLink: String?,
    @SerializedName("display_name") val name: String,
    @SerializedName("link") val profileLink: String?
)