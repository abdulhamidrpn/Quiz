package com.education.ekagratagkquiz.contribute_quiz.domain.model

import com.google.firebase.firestore.PropertyName

data class QuestionOption(
    val option: String = "",
    @PropertyName("selected")
    val isSelected: Boolean = false
)


