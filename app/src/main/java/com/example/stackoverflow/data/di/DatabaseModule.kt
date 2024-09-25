package com.example.stackoverflow.data.di

import android.content.Context
import androidx.room.Room
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.data.room.StackOverflowDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context = context, AppDatabase::class.java, "stackoverflow.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCatDao(appDatabase: AppDatabase): StackOverflowDao {
        return appDatabase.stackOverflowDao()
    }
}