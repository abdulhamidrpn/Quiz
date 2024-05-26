package com.education.ekagratagkquiz.main.domain.repository

import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.domain.models.CreateQuizResultModel
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import kotlinx.coroutines.flow.Flow

interface FullQuizRepository {

    suspend fun getAllQuestions(quiz: String): Flow<Resource<List<QuestionModel?>>>

    suspend fun getCurrentQuiz(uid: String): Resource<QuizModel?>

    suspend fun setResult(result: QuizResultModel): Flow<Resource<Boolean>>
}