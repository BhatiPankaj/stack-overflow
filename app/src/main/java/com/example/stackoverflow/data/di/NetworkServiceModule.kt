package com.example.stackoverflow.data.di

import android.content.Context
import com.example.stackoverflow.data.network.API_BASE_URL
import com.example.stackoverflow.data.network.ConnectivityInterceptor
import com.example.stackoverflow.data.network.StackOverflowNetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    @Provides
    @Singleton
    fun provideStackOverflowNetworkService(retrofit: Retrofit): StackOverflowNetworkService {
        return retrofit.create(StackOverflowNetworkService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ConnectivityInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}