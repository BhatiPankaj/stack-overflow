package com.example.stackoverflow.data.room

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTagsList(tags: List<String>?): String? {
        return tags?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toTagsList(tagsString: String?): List<String>? {
        return tagsString?.split(",") ?: emptyList()
    }
}