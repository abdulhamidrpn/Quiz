package com.education.ekagratagkquiz.main.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.core.util.formatTime
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import com.education.ekagratagkquiz.main.presentation.FullQuizViewModel
import com.education.ekagratagkquiz.main.util.FinalQuizDialogEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalQuizInfoExtra(
    attempted:Int,
    content: List<QuestionModel?>,
    modifier: Modifier = Modifier,
    viewModel: FullQuizViewModel = hiltViewModel()
) {
    if (viewModel.routeState.value.showDialog)
        AlertDialog(
            onDismissRequest = {},
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onDialogEvent(FinalQuizDialogEvents.ContinueQuiz)
                }) { Text(text = "Continue") }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDialogEvent(FinalQuizDialogEvents.SubmitQuiz) }
                ) {
                    Text(text = "Submit")
                }
            },
            title = {
                Text(
                    text = "Thanks for your participation ",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.quiz_submission_desc),
                    textAlign = TextAlign.Center
                )
            }
        )


    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {

            val currentTime = viewModel.currentTime.collectAsState(initial = null)
            val isFinished = viewModel.isFinished.collectAsState(initial = false)
            val isRunning = viewModel.isRunning.collectAsState(initial = false)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quiz will end in : ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = currentTime.value?.let { formatTime(it)  }?: "00:00",
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Slider(
                value = attempted.toFloat() / content.size,
                onValueChange = {},
                steps = content.size,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveTickColor = MaterialTheme.colorScheme.surfaceTint,
                    activeTickColor = MaterialTheme.colorScheme.primary
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "${attempted}/${content.size}",
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}