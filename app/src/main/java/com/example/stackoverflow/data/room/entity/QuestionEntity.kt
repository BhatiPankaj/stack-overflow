package com.example.stackoverflow.data.room.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stackoverflow.data.network.model.Owner
import kotlinx.parcelize.Parcelize

@Entity(tableName = "questions")
@Parcelize
data class QuestionEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val questionLink: String,
    val tags: List<String>,
    @Embedded val owner: OwnerEntity
) : Parcelable