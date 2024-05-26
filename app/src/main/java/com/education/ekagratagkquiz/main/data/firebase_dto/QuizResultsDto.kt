package com.education.ekagratagkquiz.main.data.firebase_dto

import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel

@IgnoreExtraProperties
data class QuizResultsDto(
    @DocumentId val uid: String = "",
    @get:PropertyName(FireStoreCollections.USER_NAME_FIELD)
    val userName: String? = null,
    @get:PropertyName(FireStoreCollections.QUIZ_TITLE_FIELD)
    val quizTitle: String? = null,
    @get:PropertyName(FireStoreCollections.ATTEMPTED_ANSWER)
    val attemptedAns: Int = 0,
    @get:PropertyName(FireStoreCollections.TOTAL_QUESTION_FIELD)
    val totalQuestion: Int = 0,
    @get:PropertyName(FireStoreCollections.TOTAL_MARKS_FIELD)
    val totalMarks: Double = 0.0,
    @get:PropertyName(FireStoreCollections.CORRECT_ANS_FIELD)
    val correctAns: Int = 0,
    val quizDto: QuizDto? = null
) {
    fun toModel(): QuizResultModel {
        return QuizResultModel(
            uid = uid,
            totalQuestions = totalQuestion,
            correct = correctAns,
            quiz = quizDto?.toModel(),
            quizId = quizDto?.id ?: "",
            attempt = attemptedAns,
            quizTitle = quizTitle ?: "",
            userName = userName ?: "Unknown",
        )
    }
}
