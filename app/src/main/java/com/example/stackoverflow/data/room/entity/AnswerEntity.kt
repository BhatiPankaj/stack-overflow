package com.example.stackoverflow.data.room.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey val answerId: Long,
    val questionId: Long,
    val isAccepted: Boolean,
    val body: String?,
    val score: Int,
    @Embedded val owner: OwnerEntity
) : Parcelable
