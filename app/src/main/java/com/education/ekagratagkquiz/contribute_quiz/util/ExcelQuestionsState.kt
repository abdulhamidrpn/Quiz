package com.education.ekagratagkquiz.contribute_quiz.util

import android.net.Uri

data class ExcelQuestionsState(
    val fileName: String = "",
    val fileUri: String? = null,
    val isRequestPermission: Boolean = false,
    val isError: Boolean = false,
)
sealed class ExcelQuestionEvents {
    data class readFile(val quizId: String? = null, val uri: Uri? = null) : ExcelQuestionEvents()
    data class OnDeleteConfirmed(val quizPath: String?) : ExcelQuestionEvents()
    object OnDeleteCanceled : ExcelQuestionEvents()
    object OnRequestPermission : ExcelQuestionEvents()
}
