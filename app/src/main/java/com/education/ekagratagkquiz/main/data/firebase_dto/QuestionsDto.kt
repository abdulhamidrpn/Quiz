package com.education.ekagratagkquiz.main.data.firebase_dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.main.domain.models.QuestionModel

@IgnoreExtraProperties
data class QuestionsDto(
    @DocumentId val id: String = "",
    val question: String = "",
    val description: String? = null,
    val explanation: String? = null,
    @PropertyName("required")
    val isRequired: Boolean = false,
    val options: List<QuestionOption> = emptyList(),
) {
    fun toModel(): QuestionModel {
        return QuestionModel(
            uid = id,
            question = question,
            isRequired = isRequired,
            description = description,
            questionExplanation = explanation,
            options = options
        )
    }
}
