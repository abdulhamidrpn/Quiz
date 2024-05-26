package com.education.ekagratagkquiz.main.domain.repository

import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getAllQuizzes(): Flow<Resource<List<QuizModel?>>>
    suspend fun getAllQuizzes(quiz: QuizParcelable): Flow<Resource<List<QuizModel?>>>

    suspend fun deleteQuiz(quizPath: String?="",quizId:String? = ""): Flow<Resource<Boolean>>
}