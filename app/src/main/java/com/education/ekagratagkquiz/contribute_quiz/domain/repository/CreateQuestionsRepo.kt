package com.education.ekagratagkquiz.contribute_quiz.domain.repository

import android.net.Uri
import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuestionsModel
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import kotlinx.coroutines.flow.Flow

interface CreateQuestionsRepo {

    suspend fun readExcelData(uri: Uri,quizId:String): Flow<Resource<List<CreateQuestionsModel?>>>

    suspend fun createQuestionsToQuiz(questions: List<CreateQuestionsModel>)
            : Flow<Resource<Unit>>

    suspend fun uploadImage(path: String): String
}