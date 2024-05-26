package com.education.ekagratagkquiz.contribute_quiz.domain.repository

import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuizModel
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import kotlinx.coroutines.flow.Flow

interface CreateQuizRepository {

    suspend fun createQuiz(quiz: CreateQuizModel): Flow<Resource<QuizModel?>>

    suspend fun uploadQuizImage(path: String): String

    suspend fun uploadQuizPdf(path: String): String

    suspend fun deleteQuiz(quizPath: String,quizId:String? = ""): Flow<Resource<Boolean>>
}