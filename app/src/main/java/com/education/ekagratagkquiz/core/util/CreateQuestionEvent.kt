package com.education.ekagratagkquiz.core.util

import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.OptionsEvent
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState


sealed class CreateQuestionEvent {
    object QuestionAdded : CreateQuestionEvent()
    data class ToggleQuestionType(val question: CreateQuestionState) : CreateQuestionEvent()
    data class ToggleQuestionExplanation(val question: CreateQuestionState) : CreateQuestionEvent()
    data class ToggleQuestionDesc(val question: CreateQuestionState) : CreateQuestionEvent()
    data class QuestionRemoved(val question: CreateQuestionState) : CreateQuestionEvent()
    data class ExplanationAdded(val explanation: String? = null, val question: CreateQuestionState) :
        CreateQuestionEvent()
    data class DescriptionAdded(val desc: String? = null, val question: CreateQuestionState) :
        CreateQuestionEvent()

    data class OnOptionEvent(val optionEvent: OptionsEvent, val question: CreateQuestionState) :
        CreateQuestionEvent()

    data class QuestionQuestionAdded(val value: String, val question: CreateQuestionState) :
        CreateQuestionEvent()

    data class ToggleRequiredField(val question: CreateQuestionState) : CreateQuestionEvent()

    data class SetNotEditableMode(val question: CreateQuestionState) : CreateQuestionEvent()

    data class SetEditableMode(val question: CreateQuestionState) : CreateQuestionEvent()

    data class SetCorrectOption(
        val option: QuestionOptionsState,
        val question: CreateQuestionState,
        val optionIndex: Int
    ) :
        CreateQuestionEvent()

    data class SubmitQuestions(val quidId: String) : CreateQuestionEvent()
}