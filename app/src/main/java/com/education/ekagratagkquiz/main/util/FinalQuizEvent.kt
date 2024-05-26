package com.education.ekagratagkquiz.main.util

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.main.domain.models.QuestionModel


data class FinalQuizOptionState(
    val option: SnapshotStateList<QuestionOption>? = mutableStateListOf(),
    val isCorrect: Boolean = false
)


sealed class FinalQuizEvent {
    data class OptionPicked(val index: Int, val option: QuestionOption, val question: QuestionModel) :
        FinalQuizEvent()

    data class OptionUnpicked(val index: Int) : FinalQuizEvent()

    data class OptionInputAnswer(val index: Int, val option:String) : FinalQuizEvent()

    object SubmitQuiz : FinalQuizEvent()
}
