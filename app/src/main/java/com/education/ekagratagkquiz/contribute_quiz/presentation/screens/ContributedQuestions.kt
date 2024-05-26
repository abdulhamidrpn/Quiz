package com.education.ekagratagkquiz.contribute_quiz.presentation.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.presentation.QuestionsViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.composables.NonInteractiveQuizCard
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuestionsState
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteWholeQuizState
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionEvents
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionsState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionDeleteEvent
import com.education.ekagratagkquiz.core.composables.NoContentPlaceHolder
import com.education.ekagratagkquiz.core.composables.QuizInfoParcelable
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.NavRoutes
import com.education.ekagratagkquiz.core.util.ShowContent
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributedQuestions(
    quizId: String,
    parcelable: QuizParcelable?,
    navController: NavController,
    questionsState: ShowContent<List<QuestionModel?>>,
    deleteQuizState: DeleteWholeQuizState,
    deleteQuestionState: DeleteQuestionsState,
    modifier: Modifier = Modifier,
    viewModel: QuestionsViewModel = hiltViewModel()
) {
    val snackBarState = remember { SnackbarHostState() }

    var isDropDownOpen by remember { mutableStateOf(false) }


    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> snackBarState.showSnackbar(event.message)
                UiEvent.NavigateBack -> navController.navigateUp()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Quiz Questions") },

                navigationIcon = {
                    if (navController.previousBackStackEntry != null)
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Arrow Back"
                            )
                        }
                },
                actions = {
                    IconButton(
                        onClick = { isDropDownOpen = !isDropDownOpen }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = isDropDownOpen,
                        onDismissRequest = { isDropDownOpen = !isDropDownOpen }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Delete") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Icon"
                                )
                            },
                            onClick = {
                                isDropDownOpen = !isDropDownOpen
                                viewModel.onDeleteQuizEvent(DeleteQuizEvents.PickQuiz(quizId = quizId))
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController
                        .currentBackStackEntry
                        ?.savedStateHandle?.set(NavParams.QUIZ_TAG, parcelable)
                    navController.navigate(NavRoutes.NavAddQuestionsRoute.route + "/${quizId}")
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add button")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Questions")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarState) }

    ) { padding ->

        if (deleteQuizState.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeleteQuizEvent(DeleteQuizEvents.OnDeleteCanceled) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteQuizEvent(
                                DeleteQuizEvents.OnDeleteConfirmed(
                                    quizPath = parcelable?.path
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDeleteQuizEvent(DeleteQuizEvents.OnDeleteCanceled) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                },
                title = { Text(text = "Do you want to delete this quiz") },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (deleteQuizState.isDeleting) {
                            Text(
                                text = "Deleting please wait",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.delete_full_quiz_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }
        if (deleteQuestionState.isDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.onQuestionDelete(QuestionDeleteEvent.CloseDeleteDialog)
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onQuestionDelete(QuestionDeleteEvent.DeleteConfirmed) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) { Text(text = "Delete") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onQuestionDelete(QuestionDeleteEvent.CloseDeleteDialog) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(text = "Cancel")
                    }
                },
                title = { Text(text = "Are you sure you wanna delete this") }
            )
        }
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
        ) {
            parcelable?.let { quiz ->
                QuizInfoParcelable(quiz = quiz, showId = false)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = stringResource(id = R.string.list_questions_title),
                style = MaterialTheme.typography.titleSmall,
                lineHeight = 12.sp
            )
            Text(
                text = stringResource(id = R.string.list_questions_additional),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (questionsState.isLoading) Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            else if (questionsState.content?.isNotEmpty() == true) LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(questionsState.content) { idx, item ->
                    item?.let { model ->
                        Row(
                            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${idx + 1}.",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            NonInteractiveQuizCard(
                                questionModel = model, onDelete = {
                                    viewModel.onQuestionDelete(
                                        QuestionDeleteEvent.QuestionSelected(model)
                                    )
                                }, modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .weight(.8f)
                            )
                        }
                    }
                }
                item {
                    Box(modifier = Modifier.height(50.dp))
                }
            }
            else NoContentPlaceHolder(primaryText = "No questions found",
                imageRes = R.drawable.confused,
                secondaryText = "No questions are added the quiz is blank",
                graphicsLayer = {
                    rotationX = -10f
                })
        }
    }
}


/*
private fun readExcelData(uri: Uri) {
    try {
        val contentResolver = applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return

        // Use Apache POI to read the Excel data
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0) // Access the first sheet (modify for specific needs)

        // Loop through rows and cells to process data
        for (row in sheet) {
            for (cell in row) {
                val cellValue = getCellValue(cell)
                // Process the cell value based on its type (String, number, etc.)
            }
        }
        inputStream.close()
    } catch (e: Exception) {
        Log.e("ExcelReader", "Error reading Excel file:", e)
    }
}

private fun getCellValue(cell: Cell): String {
    val cellType = cell.cellType
    return when (cellType) {
        CellType.STRING -> cell.stringCellValue
        CellType.NUMERIC -> cell.numericCellValue.toString() // Handle formatting as needed
        else -> "" // Handle other cell types or empty cells
    }
}
*/
