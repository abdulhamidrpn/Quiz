package com.education.ekagratagkquiz.main.util

import com.education.ekagratagkquiz.main.domain.models.CreateQuizResultModel
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel

data class FullQuizState(
    val isLoading: Boolean = true,
    val questions: List<QuestionModel?> = emptyList(),
    val isQuestionLoading: Boolean = true,
    val isQuizPresent: Boolean = true,
    val isAdView: Boolean = false,
    val isResultViewLoading: Boolean = false,
    val isQuizResultView: Boolean = false,
    val quizResult: QuizResultModel? = null,
    val quiz: QuizModel? = null,
    val quizModel: QuizModel? = null,
)
