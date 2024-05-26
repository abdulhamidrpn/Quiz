package com.education.ekagratagkquiz.contribute_quiz.presentation.composables

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.CreateQuestionViewModel
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.OptionsEvent
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionsViewMode
import com.education.ekagratagkquiz.core.util.CreateQuestionEvent
import com.education.ekagratagkquiz.main.domain.models.OptionType
import com.education.ekagratagkquiz.main.presentation.composables.AddExtraOptionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionCard(
    index: Int,
    question: CreateQuestionState,
    modifier: Modifier = Modifier,
    viewModel: CreateQuestionViewModel = hiltViewModel()
) {
    val TAG = "CreateQuestionCard"
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            QuestionCardHeader(
                index = index,
                question = question,
                toggleQuestionType = {

                    viewModel.onQuestionEvent(
                        CreateQuestionEvent.ToggleQuestionType(question)
                    )
                },
                toggleExplanation = {
                    viewModel.onQuestionEvent(
                        CreateQuestionEvent.ToggleQuestionExplanation(
                            question
                        )
                    )
                },
                toggleDesc = {
                    viewModel.onQuestionEvent(
                        CreateQuestionEvent.ToggleQuestionDesc(
                            question
                        )
                    )
                },
                onRemove = {
                    viewModel.onQuestionEvent(
                        CreateQuestionEvent.QuestionRemoved(
                            question
                        )
                    )
                }
            )
            Divider()
            QuestionFields(
                question = question,
                onQuestionChanged = { typedQuestion ->
                    viewModel.onQuestionEvent(
                        CreateQuestionEvent.QuestionQuestionAdded(typedQuestion, question)
                    )
                }, onDescChanged = { desc ->
                    viewModel.onQuestionEvent(CreateQuestionEvent.DescriptionAdded(desc, question))
                }, onExplanationChanged = { explanation ->
                    viewModel.onQuestionEvent(CreateQuestionEvent.ExplanationAdded(explanation, question))
                }
            )
            CreateOptions(
                question = question,
                questionOptions = question.options
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (question.state == QuestionsViewMode.Editable)
                AddExtraOptionButton(
                    onAdd = {
//                        val questionUpdated = question.copy(optionType = it)
                        Log.d(TAG, "CreateQuestionCard: $it")
                        viewModel.onQuestionEvent(
                            CreateQuestionEvent.OnOptionEvent(
                                OptionsEvent.OptionAdded(it), question
                            )
                        )
                    },
                )
            Divider()
            QuestionCardFooter(
                questionState = question,
                onToggle = {
                    viewModel.onQuestionEvent(CreateQuestionEvent.ToggleRequiredField(question))
                },
                onAnsKey = {
                    viewModel.onQuestionEvent(CreateQuestionEvent.SetNotEditableMode(question))
                },
                onDone = {
                    viewModel.onQuestionEvent(CreateQuestionEvent.SetEditableMode(question))
                }
            )
        }
    }
}

