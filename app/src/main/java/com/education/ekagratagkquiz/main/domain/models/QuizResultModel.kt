package com.education.ekagratagkquiz.main.domain.models

data class QuizResultModel(
    val uid: String = "",
    val quizId: String,
    val userName: String = "",
    val quizTitle: String,
    val totalQuestions: Int,
    val correct: Int,
    val attempt: Int,
    val quiz: QuizModel? = null
) {
    private val skippedOption = totalQuestions - attempt
    private val wrongOption = attempt - correct
    val finalResult = correct - (wrongOption * 0.25)
    val finalOutput = "" +
            "\nSkipped  = $skippedOption" +
            "\nCorrect   = $correct" +
            "\nWrong     = $wrongOption" +
            "\nMark        = $finalResult / $totalQuestions"

    val finalUniversalOutput = "" +
            "\nSkipped  = $skippedOption" +
            "\nMark        = $finalResult / $totalQuestions"

}
