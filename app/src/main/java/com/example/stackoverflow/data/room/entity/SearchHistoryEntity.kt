package com.example.stackoverflow.data.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "search_history", indices = [
    Index(value = ["searchedText"], unique = true)
])
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val searchedText: String
)