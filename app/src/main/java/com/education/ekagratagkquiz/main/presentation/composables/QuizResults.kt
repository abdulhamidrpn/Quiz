package com.education.ekagratagkquiz.main.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import com.education.ekagratagkquiz.main.presentation.ResultsScreenViewModel
import com.education.ekagratagkquiz.main.presentation.composables.result.QuizResultsCard
import com.education.ekagratagkquiz.main.presentation.composables.result.QuizResultsUniversalCard
import com.education.ekagratagkquiz.main.presentation.screens.HomeScreenCompact
import com.education.ekagratagkquiz.main.presentation.screens.HomeScreenExpanded
import com.education.ekagratagkquiz.main.util.DeleteQuizResultsEvent
import com.education.ekagratagkquiz.main.util.DeleteQuizResultsState

@Composable
fun QuizResults(
    content: List<QuizResultModel?>,
    state:DeleteQuizResultsState,
    onDeleteConfirm: () -> Unit,
    onDeleteCancelled: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultsScreenViewModel = hiltViewModel(),
    isExpandedScreen: Boolean = false
) {
    if (state.isDialogOpen)
        AlertDialog(
            onDismissRequest = onDeleteCancelled,
            confirmButton = {
                Button(
                    onClick = onDeleteConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Delete")
                }
            }, dismissButton = {
                TextButton(
                    onClick = onDeleteCancelled,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Cancel")
                }
            },
            title = { Text(text = "Delete Result", style = MaterialTheme.typography.titleLarge) },
            text = {
                val quizName = state.result?.quiz?.subject ?: ""
                Text(text = "Are you sure you wanna delete the result for the quiz $quizName")
            },
            titleContentColor = MaterialTheme.colorScheme.error
        )
    Text(
        text = buildAnnotatedString {
            append("You have participated in ")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("${content.size}")
            }
            append(" quizzes")
        },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    )



    if (isExpandedScreen) {
        /*View for landscape*/

        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(content) { _, item ->
                item?.let { model ->
                    QuizResultsCard(
                        result = model,
                        onDelete = {
                            viewModel
                                .onDeleteResult(DeleteQuizResultsEvent.ResultsSelected(model))
                        }
                    )
                }
            }
        }
    } else {
        /*View for Portrait Compact*/

        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(content) { _, item ->
                item?.let { model ->
                    QuizResultsCard(
                        result = model,
                        onDelete = {
                            viewModel
                                .onDeleteResult(DeleteQuizResultsEvent.ResultsSelected(model))
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun QuizResultsUniversal(
    content: List<QuizResultModel?>,
    state:DeleteQuizResultsState,
    onDeleteConfirm: () -> Unit,
    onDeleteCancelled: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultsScreenViewModel = hiltViewModel()
) {
    if (state.isDialogOpen)
        AlertDialog(
            onDismissRequest = onDeleteCancelled,
            confirmButton = {
                Button(
                    onClick = onDeleteConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDeleteCancelled,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(text = "Cancel")
                }
            },
            title = { Text(text = "Delete Result", style = MaterialTheme.typography.titleLarge) },
            text = {
                val quizName = state.result?.quiz?.subject ?: ""
                Text(text = "Are you sure you wanna delete the result for the quiz $quizName")
            },
            titleContentColor = MaterialTheme.colorScheme.error
        )
    Text(
        text = buildAnnotatedString {
            append("Participator Result ")
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("${content.size}")
            }
            append(" quizzes")
        },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    )

    if (false) {
        /*View for landscape*/

        LazyVerticalStaggeredGrid(
            modifier = modifier.padding(4.dp),
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 4.dp,
        ) {
            itemsIndexed(content) { _, item ->
                item?.let { model ->
                    QuizResultsUniversalCard(
                        result = model,
                        onDelete = {
                            viewModel
                                .onDeleteResult(DeleteQuizResultsEvent.ResultsSelected(model))
                        }
                    )
                }
            }
        }
    } else {
        /*View for Portrait Compact*/

        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(content) { _, item ->
                item?.let { model ->
                    QuizResultsUniversalCard(
                        result = model,
                        onDelete = {
                            viewModel
                                .onDeleteResult(DeleteQuizResultsEvent.ResultsSelected(model))
                        }
                    )
                }
            }
        }
    }
}