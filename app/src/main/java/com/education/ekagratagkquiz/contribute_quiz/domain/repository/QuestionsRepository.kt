package com.education.ekagratagkquiz.contribute_quiz.domain.repository

import android.net.Uri
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import kotlinx.coroutines.flow.Flow

interface QuestionsRepository {

    suspend fun getQuestions(quiz: String): Flow<Resource<List<QuestionModel?>>>

    suspend fun deleteQuestion(questionModel: QuestionModel): Flow<Resource<Boolean>>

    suspend fun deleteQuiz(quizId: String): Flow<Resource<Boolean>>
}