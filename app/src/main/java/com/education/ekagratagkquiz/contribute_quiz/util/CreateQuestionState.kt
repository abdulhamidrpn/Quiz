package com.education.ekagratagkquiz.contribute_quiz.util

import androidx.compose.runtime.mutableStateListOf
import com.education.ekagratagkquiz.main.domain.models.OptionType
import com.education.ekagratagkquiz.main.domain.models.QuestionType
import com.education.ekagratagkquiz.main.domain.models.SelectionType

data class CreateQuestionState(
    val question: String = "",
    val questionError: String? = null,
    val desc: String? = null,
    val questionExplanation: String? = null,
    val required: Boolean = false,
    val selectionType: SelectionType = SelectionType.SingleChoice,/*1.Text 2.Image 3. True/False 4.Input type*/
    val optionType: OptionType = OptionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val questionType: QuestionType = QuestionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val state: QuestionsViewMode = QuestionsViewMode.Editable,
    val isVerified: Boolean = false,
    val isDeleteAllowed: Boolean = true,
    val options: MutableList<QuestionOptionsState> = mutableStateListOf(QuestionOptionsState())
)

