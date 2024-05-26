package com.education.ekagratagkquiz.contribute_quiz.domain.model


import com.education.ekagratagkquiz.main.domain.models.OptionType
import com.education.ekagratagkquiz.main.domain.models.QuestionType
import com.education.ekagratagkquiz.main.domain.models.SelectionType

data class CreateQuestionsModel(
    val question: String,
    val description: String? = null,
    val isSourceExcel: Boolean = false,
    val isRequired: Boolean = false,
    val selectionType: SelectionType = SelectionType.SingleChoice,
    val optionType: OptionType = OptionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val questionType: QuestionType = QuestionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val questionExplanation: String? = null,
    val options: List<QuestionOption> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val correctAnswer: String? = null,
    val quizId: String? = null
)








