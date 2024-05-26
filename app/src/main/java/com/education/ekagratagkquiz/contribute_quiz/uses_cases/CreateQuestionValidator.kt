package com.education.ekagratagkquiz.contribute_quiz.uses_cases

import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.core.models.Validator

class CreateQuestionValidator {

    fun validateQuestion(state: CreateQuestionState): Validator {
        return if (state.question.isEmpty()) Validator(
            isValid = false,
            message = "Cannot add a blank question"
        ) else if (state.question.length < 5)
            Validator(isValid = false, message = "The questions is to small")
        else Validator(isValid = true)
    }


    fun validateOptions(state: QuestionOptionsState): Validator {
        return if (state.option.isEmpty()) Validator(
            isValid = false,
            message = "Cannot add a blank option"
        ) else Validator(isValid = true)
    }
}