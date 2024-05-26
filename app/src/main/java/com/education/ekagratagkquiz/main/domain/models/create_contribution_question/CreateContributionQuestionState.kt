package com.education.ekagratagkquiz.main.domain.models.create_contribution_question

import androidx.compose.runtime.mutableStateListOf
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionsViewMode
import com.education.ekagratagkquiz.main.domain.models.OptionType
import com.education.ekagratagkquiz.main.domain.models.QuestionType
import com.education.ekagratagkquiz.main.domain.models.SelectionType

data class CreateContributionQuestionState(
    val question: Content = Content.TextContent(""),
    val type: Quiz = Quiz.SingleChoice,
    val answer: String? = null, // Only for UserInput
    val quizId: String? = null,

    val questionError: String? = null,
    val desc: String? = null,
    val required: Boolean = false,
    val selectionType: SelectionType = SelectionType.SingleChoice,/*1.Text 2.Image 3. True/False 4.Input type*/
    val optionType: OptionType = OptionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val questionType: QuestionType = QuestionType.TEXT,/*1.Text 2.Image 3. True/False 4.Input type*/
    val state: QuestionsViewMode = QuestionsViewMode.Editable,
    val isVerified: Boolean = false,
    val isDeleteAllowed: Boolean = true,
    val options: MutableList<CreateContributionOptionsState> = mutableStateListOf(CreateContributionOptionsState())
)

