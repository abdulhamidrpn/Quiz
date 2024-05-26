package com.education.ekagratagkquiz.main.util

import com.education.ekagratagkquiz.main.domain.models.QuizResultModel


data class DeleteQuizResultsState(
    val isDialogOpen: Boolean = false,
    val result: QuizResultModel? = null
)

sealed class DeleteQuizResultsEvent {
    data class ResultsSelected(val result: QuizResultModel) : DeleteQuizResultsEvent()
    object DeleteConfirmed : DeleteQuizResultsEvent()
    object DeleteCanceled : DeleteQuizResultsEvent()
}
