package com.education.ekagratagkquiz.main.presentation.composables.result
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.education.ekagratagkquiz.contribute_quiz.presentation.composables.OptionImagePicker
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionsViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultOptionBlock(
    optionIndex: Int,
    item: QuestionOptionsState,
    state: QuestionsViewMode,
    modifier: Modifier = Modifier,
    ansKey: QuestionOptionsState? = null,
    selectCorrectOption: () -> Unit,
    onOptionRemove: () -> Unit,
    onOptionValueChange: (String) -> Unit,
) {
    Row(
        modifier = when (state) {
            QuestionsViewMode.NonEditable -> modifier
                .fillMaxWidth()
                .background(
                    color = if (item.isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    1.2f.dp,
                    color = if (item.isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .clickable(onClick = selectCorrectOption)

            else -> modifier
                .fillMaxWidth()
                .padding(4.dp)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        //An icon can be used but the actual thing has a different feeling,
        // I meant this for the radio button 😊
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .weight(.025f)
        )
        RadioButton(
            selected = item.isSelected,
            onClick = when (state) {
                QuestionsViewMode.NonEditable -> selectCorrectOption
                else -> {
                    {}
                }
            },
            modifier = Modifier
                .weight(0.1f)
                .padding(4.dp, 0.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.weight(.025f))
        when (state) {
            QuestionsViewMode.Editable -> {
                Column(
                    modifier = Modifier.weight(.9f)
                ) {
                    OutlinedTextField(
                        value = item.option,
                        onValueChange = onOptionValueChange,
                        maxLines = 1,
                        placeholder = { Text(text = "Option $optionIndex") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = onOptionRemove) {
                                Icon(
                                    imageVector = Icons.Outlined.RemoveCircleOutline,
                                    contentDescription = "Remove this option"
                                )
                            }
                        }
                    )
                    item.optionError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            else -> {
                Text(
                    text = item.option, modifier = Modifier
                        .weight(.85f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultOptionBlockImage(
    optionIndex: Int,
    item: QuestionOptionsState,
    state: QuestionsViewMode,
    modifier: Modifier = Modifier,
    ansKey: QuestionOptionsState? = null,
    selectCorrectOption: () -> Unit,
    onOptionRemove: () -> Unit,
    onOptionValueChange: (String) -> Unit,
) {
    Row(
        modifier = when (state) {
            QuestionsViewMode.NonEditable -> modifier
                .fillMaxWidth()
                .background(
                    color = if (item.isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    1.2f.dp,
                    color = if (item.isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .clickable(onClick = selectCorrectOption)

            else -> modifier
                .fillMaxWidth()
                .padding(4.dp)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        //An icon can be used but the actual thing has a different feeling,
        // I meant this for the radio button 😊
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .weight(.025f)
        )
        RadioButton(
            selected = item.isSelected,
            onClick = when (state) {
                QuestionsViewMode.NonEditable -> selectCorrectOption
                else -> {
                    {}
                }
            },
            modifier = Modifier
                .weight(0.1f)
                .padding(4.dp, 0.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.weight(.025f))
        when (state) {
            QuestionsViewMode.Editable -> {
                Column(
                    modifier = Modifier.weight(.9f)
                ) {
                    OptionImagePicker(
                        image = item.option,
                        optionIndex = optionIndex,
                        onImageChanged = {
                            onOptionValueChange(it)
                        },
                        onImageOptionRemoved = {
                            onOptionRemove()
                        }
                    )

                    item.optionError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            else -> {
                Text(
                    text = item.option, modifier = Modifier
                        .weight(.85f)
                        .fillMaxWidth()
                )
                /*OptionImage(
                    modifier = Modifier
                        .weight(.85f)
                        .fillMaxWidth(),
                    optionIndex = optionIndex,
                    image = item.option
                )*/
            }
        }
    }
}



@Composable
fun ResultInputBlock(
    item: QuestionOptionsState,
    state: QuestionsViewMode,
    modifier: Modifier = Modifier,
    onOptionRemove: () -> Unit,
    onOptionValueChange: (String) -> Unit,
) {
    Row(
        modifier = when (state) {
            QuestionsViewMode.NonEditable -> modifier
                .fillMaxWidth()
                .background(
                    color = if (item.isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    1.2f.dp,
                    color = if (item.isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )

            else -> modifier
                .fillMaxWidth()
                .padding(4.dp)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        //An icon can be used but the actual thing has a different feeling,
        // I meant this for the radio button 😊
        Spacer(
            modifier = Modifier
                .width(4.dp)
                .weight(.025f)
        )
        when (state) {
            QuestionsViewMode.Editable -> {
                Column(
                    modifier = Modifier.weight(.9f)
                ) {
                    OutlinedTextField(
                        value = item.option,
                        onValueChange = onOptionValueChange,
                        maxLines = 2,
                        placeholder = { Text(text = "Input your answer.") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = onOptionRemove) {
                                Icon(
                                    imageVector = Icons.Outlined.RemoveCircleOutline,
                                    contentDescription = "Remove this answer"
                                )
                            }
                        }
                    )
                    item.optionError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            else -> {
                Text(
                    text = item.option, modifier = Modifier
                        .weight(.85f)
                        .fillMaxWidth()
                )
            }
        }
    }
}
