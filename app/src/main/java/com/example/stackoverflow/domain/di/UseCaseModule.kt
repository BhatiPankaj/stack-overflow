package com.example.stackoverflow.domain.di

import com.example.stackoverflow.data.network.StackOverflowNetworkService
import com.example.stackoverflow.data.repository.getanswerssrepository.AnswerRepository
import com.example.stackoverflow.data.repository.getanswerssrepository.AnswerRepositoryImpl
import com.example.stackoverflow.data.repository.getquestionsrepository.QuestionRepository
import com.example.stackoverflow.data.repository.getquestionsrepository.QuestionRepositoryImpl
import com.example.stackoverflow.data.repository.searchquestionrepository.SearchQuestionRepository
import com.example.stackoverflow.data.repository.searchquestionrepository.SearchQuestionRepositoryImpl
import com.example.stackoverflow.data.room.AppDatabase
import com.example.stackoverflow.domain.usecase.GetAnswersUseCase
import com.example.stackoverflow.domain.usecase.GetQuestionsUseCase
import com.example.stackoverflow.domain.usecase.SearchUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun getQuestionsRepository(
        networkService: StackOverflowNetworkService,
        appDatabase: AppDatabase
    ): QuestionRepository {
        return QuestionRepositoryImpl(networkService, appDatabase)
    }

    @Provides
    @Singleton
    fun getQuestionUseCase(repository: QuestionRepository) : GetQuestionsUseCase {
        return GetQuestionsUseCase(repository)
    }

    @Provides
    @Singleton
    fun getSearchRepository(networkService: StackOverflowNetworkService, appDatabase: AppDatabase) : SearchQuestionRepository {
        return SearchQuestionRepositoryImpl(networkService, appDatabase)
    }

    @Provides
    @Singleton
    fun getSearchUseCase(repository: SearchQuestionRepository) : SearchUseCase {
        return SearchUseCase(repository)
    }

    @Provides
    @Singleton
    fun getAnswersRepository(
        networkService: StackOverflowNetworkService,
        appDatabase: AppDatabase
    ): AnswerRepository {
        return AnswerRepositoryImpl(networkService, appDatabase)
    }

    @Provides
    @Singleton
    fun getAnswerUseCase(repository: AnswerRepository) : GetAnswersUseCase {
        return GetAnswersUseCase(repository)
    }
}