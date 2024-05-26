package com.education.ekagratagkquiz.contribute_quiz.util

import com.education.ekagratagkquiz.main.domain.models.QuestionModel


data class DeleteQuestionsState(
    val isDialogOpen: Boolean = false,
    val model: QuestionModel? = null
)

sealed class QuestionDeleteEvent {
    data class QuestionSelected(val model: QuestionModel) : QuestionDeleteEvent()
    object DeleteConfirmed : QuestionDeleteEvent()
    object CloseDeleteDialog : QuestionDeleteEvent()
}
