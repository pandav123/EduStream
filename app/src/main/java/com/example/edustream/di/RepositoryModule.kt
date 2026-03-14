package com.example.edustream.di

import com.example.edustream.features.auth.data.repository.AuthRepositoryImpl
import com.example.edustream.features.auth.domain.repository.AuthRepository
import com.example.edustream.features.marketplace.data.repository.CourseRepositoryImpl
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.features.progress.data.repository.ProgressRepositoryImpl
import com.example.edustream.features.progress.domain.repository.ProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        courseRepositoryImpl: CourseRepositoryImpl
    ): CourseRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository
}
