package com.education.ekagratagkquiz.main.di

import com.education.ekagratagkquiz.main.data.repository.FullQuizRepoImpl
import com.education.ekagratagkquiz.main.data.repository.HomeRepositoryImpl
import com.education.ekagratagkquiz.main.data.repository.PreparationRepositoryImpl
import com.education.ekagratagkquiz.main.data.repository.QuizResultRepoImpl
import com.education.ekagratagkquiz.main.domain.repository.FullQuizRepository
import com.education.ekagratagkquiz.main.domain.repository.HomeRepository
import com.education.ekagratagkquiz.main.domain.repository.PreparationRepository
import com.education.ekagratagkquiz.main.domain.repository.QuizResultsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class QuizRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun getQuizRepoInstance(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @ViewModelScoped
    abstract fun getFullQuizRepo(impl: FullQuizRepoImpl): FullQuizRepository

    @Binds
    @ViewModelScoped
    abstract fun resultsRepo(impl: QuizResultRepoImpl): QuizResultsRepository

    @Binds
    @ViewModelScoped
    abstract fun preparationRepo(impl: PreparationRepositoryImpl): PreparationRepository

}
