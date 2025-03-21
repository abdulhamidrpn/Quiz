package com.education.ekagratagkquiz.contribute_quiz.di

import com.education.ekagratagkquiz.contribute_quiz.data.repository.CreateQuestionRepoImpl
import com.education.ekagratagkquiz.contribute_quiz.data.repository.CreateQuizRepoImpl
import com.education.ekagratagkquiz.contribute_quiz.data.repository.QuestionRepoImpl
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuestionsRepo
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuizRepository
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.QuestionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class ContributeQuizModule {

    @Binds
    @ViewModelScoped
    abstract fun quizQuizInstance(repo: CreateQuizRepoImpl): CreateQuizRepository

    @Binds
    @ViewModelScoped
    abstract fun createQuestionsRepo(repo: CreateQuestionRepoImpl): CreateQuestionsRepo

    @Binds
    @ViewModelScoped
    abstract fun questionRepo(repo: QuestionRepoImpl): QuestionsRepository

}