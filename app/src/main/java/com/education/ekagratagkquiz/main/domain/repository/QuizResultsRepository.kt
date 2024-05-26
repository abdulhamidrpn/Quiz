package com.education.ekagratagkquiz.main.domain.repository

import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import kotlinx.coroutines.flow.Flow

interface QuizResultsRepository {

    suspend fun getQuizResults(): Flow<Resource<List<QuizResultModel?>>>

    suspend fun getQuizResults(quizId:String): Flow<Resource<List<QuizResultModel?>>>

    suspend fun deleteQuizResults(resultId: String): Resource<Unit>
}
