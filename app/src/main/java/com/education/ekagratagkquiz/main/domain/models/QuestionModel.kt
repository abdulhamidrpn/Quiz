package com.education.ekagratagkquiz.main.domain.models

import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption

data class QuestionModel(
    val uid: String,
    val question: String,
    val description: String? = null,
    val questionExplanation: String? = null,
    val type: QuestionType = QuestionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val questionType: QuestionType = QuestionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val optionType: OptionType = OptionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val isRequired: Boolean = false,
    val options: List<QuestionOption>
) {
    val shuffledOptions = options.shuffled()
}
