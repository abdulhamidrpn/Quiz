package com.education.ekagratagkquiz.contribute_quiz.util

data class DeleteWholeQuizState(
    val showDialog: Boolean = false,
    val quizId: String? = null,
    val quizPath: String? = null,
    val isDeleting: Boolean = false
)

sealed class DeleteQuizEvents {
    data class PickQuiz(val quizId: String? = null, val quizPath: String? = null) : DeleteQuizEvents()
    data class OnDeleteConfirmed(val quizPath: String?) : DeleteQuizEvents()
    object OnDeleteCanceled : DeleteQuizEvents()
}
