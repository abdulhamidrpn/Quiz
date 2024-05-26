package com.education.ekagratagkquiz.main.util.contribution
import com.education.ekagratagkquiz.contribute_quiz.util.OptionsEvent
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.CreateContributionOptionsEvent
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.CreateContributionOptionsState
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.CreateContributionQuestionState


sealed class CreateContributionQuestionEvent {
    object QuestionAdded : CreateContributionQuestionEvent()
    data class ToggleQuestionType(val question: CreateContributionQuestionState) : CreateContributionQuestionEvent()
    data class ToggleQuestionDesc(val question: CreateContributionQuestionState) : CreateContributionQuestionEvent()
    data class QuestionRemoved(val question: CreateContributionQuestionState) : CreateContributionQuestionEvent()
    data class DescriptionAdded(val desc: String? = null, val question: CreateContributionQuestionState) :
        CreateContributionQuestionEvent()

    data class OnOptionEvent(val optionEvent: CreateContributionOptionsEvent, val question: CreateContributionQuestionState) :
        CreateContributionQuestionEvent()

    data class QuestionQuestionAdded(val value: String, val question: CreateContributionQuestionState) :
        CreateContributionQuestionEvent()

    data class ToggleRequiredField(val question: CreateContributionQuestionState) : CreateContributionQuestionEvent()

    data class SetNotEditableMode(val question: CreateContributionQuestionState) : CreateContributionQuestionEvent()

    data class SetEditableMode(val question: CreateContributionQuestionState) : CreateContributionQuestionEvent()

    data class SetCorrectOption(
        val option: CreateContributionOptionsState,
        val question: CreateContributionQuestionState
    ) :
        CreateContributionQuestionEvent()

    data class SubmitQuestions(val quidId: String) : CreateContributionQuestionEvent()
}