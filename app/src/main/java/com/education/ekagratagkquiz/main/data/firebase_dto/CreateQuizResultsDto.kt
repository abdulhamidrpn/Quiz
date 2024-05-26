package com.education.ekagratagkquiz.main.data.firebase_dto

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections

data class CreateQuizResultsDto(
    @get:PropertyName(FireStoreCollections.QUIZ_ID_FIELD)
    val quizId: DocumentReference? = null,
    @get:PropertyName(FireStoreCollections.QUIZ_TITLE_FIELD)
    val quizTitle: String? = null,
    @get:PropertyName(FireStoreCollections.USER_ID_FIELD)
    val userId: String? = null,
    @get:PropertyName(FireStoreCollections.USER_NAME_FIELD)
    val userName: String? = null,
    @get:PropertyName(FireStoreCollections.TOTAL_QUESTION_FIELD)
    val totalQuestions: Int = 0,
    @get:PropertyName(FireStoreCollections.CORRECT_ANS_FIELD)
    val correctAnswer: Int = 0,
    @get:PropertyName(FireStoreCollections.TOTAL_MARKS_FIELD)
    val totalMarks: Double = 0.0,
    @get:PropertyName(FireStoreCollections.ATTEMPTED_ANSWER)
    val attemptedAnswer: Int = 0,
) {

    fun updateMap(): Map<String, Any> =
        hashMapOf(
            FireStoreCollections.TOTAL_QUESTION_FIELD to totalQuestions,
            FireStoreCollections.ATTEMPTED_ANSWER to attemptedAnswer,
            FireStoreCollections.TOTAL_MARKS_FIELD to totalMarks,
            FireStoreCollections.CORRECT_ANS_FIELD to correctAnswer
        )

}

