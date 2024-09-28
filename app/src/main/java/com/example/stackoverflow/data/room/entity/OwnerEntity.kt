package com.example.stackoverflow.data.room.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("owners")
data class OwnerEntity(
    @PrimaryKey val accountId: Int,
    val userId: Int,
    val profileImageLink: String?,
    val name: String,
    val profileLink: String?
) : Parcelable