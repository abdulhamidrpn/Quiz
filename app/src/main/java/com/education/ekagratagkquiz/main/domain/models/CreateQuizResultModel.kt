package com.education.ekagratagkquiz.main.domain.models

data class CreateQuizResultModel(
    val quizTitle: String,
    val quizId: String,
    val totalQuestions: Int,
    val correct: Int,
    val attempt: Int
)
