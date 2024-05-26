package com.education.ekagratagkquiz.contribute_quiz.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionsViewMode
import com.education.ekagratagkquiz.main.domain.models.QuestionType

@Composable
fun QuestionCardHeader(
    index: Int,
    question: CreateQuestionState,
    modifier: Modifier = Modifier,
    toggleQuestionType: () -> Unit,
    toggleExplanation: () -> Unit,
    toggleDesc: () -> Unit,
    onRemove: () -> Unit
) {
    var toggleDropDown by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Question ${index + 1}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Box(
            modifier = Modifier.fillMaxWidth(.25f)
        ) {
            IconButton(
                onClick = { toggleDropDown = !toggleDropDown }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Extra options"
                )
            }
            DropdownMenu(
                expanded = toggleDropDown,
                onDismissRequest = { toggleDropDown = !toggleDropDown },
                properties = PopupProperties(
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true,
                ),
            ) {
                DropdownMenuItem(
                    enabled = question.state == QuestionsViewMode.Editable,
                    text = {
                        if (question.questionType == QuestionType.IMAGE)
                            Text(text = "Add Text Question")
                        else
                            Text(text = "Add Image Question")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Image or Text Question"
                        )
                    },
                    onClick = toggleQuestionType,
                )
                DropdownMenuItem(
                    enabled = question.state == QuestionsViewMode.Editable,
                    text = {
                        if (question.desc == null)
                            Text(text = "Add Description")
                        else
                            Text(text = "Remove Description")
                    },
                    leadingIcon = {
                        if (question.desc == null)
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Description"
                            )
                        else Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove Description"
                        )
                    },
                    onClick = toggleDesc,
                )
                DropdownMenuItem(
                    enabled = question.state == QuestionsViewMode.Editable,
                    text = {
                        if (question.questionExplanation == null)
                            Text(text = "Add Explanation")
                        else
                            Text(text = "Remove Explanation")
                    },
                    leadingIcon = {
                        if (question.questionExplanation == null)
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Explanation"
                            )
                        else Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove Explanation"
                        )
                    },
                    onClick = toggleExplanation,
                )
                DropdownMenuItem(
                    enabled = question.isDeleteAllowed,
                    text = { Text(text = "Remove Question") },
                    onClick = onRemove,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = "Remove Icon"
                        )
                    }
                )
            }
        }
    }
}