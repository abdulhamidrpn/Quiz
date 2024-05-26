package com.education.ekagratagkquiz.contribute_quiz.data.dto

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.main.domain.models.OptionType
import com.education.ekagratagkquiz.main.domain.models.QuestionType
import com.education.ekagratagkquiz.main.domain.models.SelectionType

data class CreateQuestionDto(
    val question: String = "",
    val description: String? = null,
    val explanation: String? = null,
    val selectionType: String? = SelectionType.SingleChoice.name,/*1.Text 2.Image 3. True/False 4.Input type*/
    @PropertyName("required")
    val isRequired: Boolean = false,
    val options: List<QuestionOption> = emptyList(),
    val quizId: DocumentReference? = null
)

