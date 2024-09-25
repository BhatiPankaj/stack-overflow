package com.example.stackoverflow.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.data.room.entity.QuestionEntity

@Database(entities = [QuestionEntity::class, AnswerEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stackOverflowDao(): StackOverflowDao
}