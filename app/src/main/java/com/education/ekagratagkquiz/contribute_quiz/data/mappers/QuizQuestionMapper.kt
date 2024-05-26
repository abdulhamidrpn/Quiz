package com.education.ekagratagkquiz.contribute_quiz.data.mappers

import com.google.firebase.firestore.FirebaseFirestore
import com.education.ekagratagkquiz.contribute_quiz.data.dto.CreateQuestionDto
import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuestionsModel
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.main.domain.models.QuestionModel

fun CreateQuestionState.toModel(): CreateQuestionsModel = CreateQuestionsModel(
    question = questionType.name + ":" + question,
    description = desc,
    questionExplanation = questionExplanation,
    isRequired = required,
    selectionType = selectionType,
    questionType = questionType,
    optionType = optionType,
    options = options.map {
        QuestionOption(
            option = "${it.optionType.name}:${it.option}",
            isSelected = it.isSelected,
        )
    }
)

fun CreateQuestionsModel.toState(): CreateQuestionState {
    return CreateQuestionState(
        question = question,
        desc = description,
        questionExplanation = questionExplanation,
        required = isRequired,
        selectionType = selectionType,
        options = options.map {
            QuestionOptionsState(
                option = it.option,
                isSelected = it.isSelected
            )
        }.toMutableList(),
    )
}
fun QuestionModel.toState(): CreateQuestionState {
    return CreateQuestionState(
        question = question,
        desc = description,
        questionExplanation = questionExplanation,
        required = isRequired,
        options = options.map {
            QuestionOptionsState(
                option = it.option,
                isSelected = it.isSelected
            )
        }.toMutableList(),
    )
}

fun CreateQuestionsModel.toDto(
    fireStore: FirebaseFirestore
): CreateQuestionDto {
    return CreateQuestionDto(
        question = question,
        description = description,
        explanation = questionExplanation,
        isRequired = isRequired,
        selectionType = selectionType.name,
        options = options,
        quizId = quizId?.let { id ->
            fireStore.document(FireStoreCollections.QUIZ_COLLECTION + "/$id")
        }
    )
}