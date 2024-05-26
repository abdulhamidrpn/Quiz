package com.education.ekagratagkquiz.contribute_quiz.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.CreateQuestionViewModel
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.OptionsEvent
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionsViewMode
import com.education.ekagratagkquiz.core.util.CreateQuestionEvent
import com.education.ekagratagkquiz.main.domain.models.OptionType

@Composable
fun CreateOptions(
    question: CreateQuestionState,
    questionOptions: MutableList<QuestionOptionsState>,
    modifier: Modifier = Modifier,
    viewModel: CreateQuestionViewModel = hiltViewModel(),
    focusManager: FocusManager = LocalFocusManager.current
) {
    LaunchedEffect(question.state) {
        if (question.state == QuestionsViewMode.NonEditable) {
            focusManager.clearFocus()
        }
    }

    Column {
        for ((index, item) in questionOptions.mapIndexed { index, item -> index to item }) {
            when (item.optionType) {
                OptionType.TEXT -> {
                    CreateOptionBlock(
                        optionIndex = index + 1,
                        item = item,
                        state = question.state,
                        selectCorrectOption = {
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.SetCorrectOption(item, question,index)
                            )
                        },
                        onOptionRemove = {
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.OnOptionEvent(
                                    OptionsEvent.OptionRemove(item), question
                                )
                            )
                        },
                        onOptionValueChange = { option ->
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.OnOptionEvent(
                                    OptionsEvent.OptionValueChanged(
                                        option, index
                                    ), question
                                )
                            )
                        },
                        modifier = modifier
                    )
                }

                OptionType.IMAGE -> {
                    CreateOptionBlockImage(
                        optionIndex = index + 1,
                        item = item,
                        state = question.state,
                        selectCorrectOption = {
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.SetCorrectOption(item, question,index)
                            )
                        },
                        onOptionRemove = {
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.OnOptionEvent(
                                    OptionsEvent.OptionRemove(item), question
                                )
                            )
                        },
                        onOptionValueChange = { option ->
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.OnOptionEvent(
                                    OptionsEvent.OptionValueChanged(
                                        option, index
                                    ), question
                                )
                            )
                        },
                        modifier = modifier
                    )
                }

                else -> {

                    CreateInputBlock(
                        item = item,
                        state = question.state,
                        onOptionRemove = {
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.OnOptionEvent(
                                    OptionsEvent.OptionRemove(item), question
                                )
                            )
                        },
                        onOptionValueChange = { option ->
                            viewModel.onQuestionEvent(
                                CreateQuestionEvent.OnOptionEvent(
                                    OptionsEvent.OptionValueChanged(
                                        option, index
                                    ), question
                                )
                            )
                        },
                        modifier = modifier
                    )
                }

            }
        }
    }
}

