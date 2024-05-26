package com.education.ekagratagkquiz.contribute_quiz.presentation.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.presentation.CreateQuestionViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.composables.CreateQuestionCard
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionEvents
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionsState
import com.education.ekagratagkquiz.core.composables.QuizInfoParcelable
import com.education.ekagratagkquiz.core.util.CreateQuestionEvent
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import kotlinx.coroutines.flow.collectLatest


@Preview
@Composable
private fun CreateQuestionPreview() {

    CreateQuestions(quizId = "", parcelable = null, navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestions(
    quizId: String,
    parcelable: QuizParcelable?,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CreateQuestionViewModel = hiltViewModel(),
    excelQuestionsState: ExcelQuestionsState = ExcelQuestionsState(),
) {

    // TODO: Edit Question.

    var isDropDownOpen by remember { mutableStateOf(false) }

    val questions = viewModel.questions

    val snackBarState = remember { SnackbarHostState() }

    val excelFilePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onExcelQuestionEvents(ExcelQuestionEvents.readFile(uri = result.data?.data, quizId = quizId))
            }
        }
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
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Add Questions") },
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
                },actions = {
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
                            text = { Text(text = "Upload Excel") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.UploadFile,
                                    contentDescription = "Upload excel file"
                                )
                            },
                            onClick = {
                                isDropDownOpen = !isDropDownOpen
                                viewModel.onExcelQuestionEvents(ExcelQuestionEvents.OnRequestPermission)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.onQuestionEvent(CreateQuestionEvent.SubmitQuestions(quizId))
                }
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save the questions")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Save")
            }
        }
    ) { padding ->
        if (viewModel.showDialog.value)
            AlertDialog(
                onDismissRequest = {},
                title = { Text(text = "Adding Questions", textAlign = TextAlign.Center) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.adding_question_wait),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                },
                confirmButton = {}
            )

        if (excelQuestionsState.isRequestPermission) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // Set the MIME type to filter files
//                type = "application/vnd.ms-excel" // MIME type for Excel files
            }
            excelFilePicker.launch(intent)
        }
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
        ) {
            parcelable?.let { quiz ->
                QuizInfoParcelable(quiz = quiz, showId = false)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = stringResource(id = R.string.add_quiz_question_text),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            LazyColumn(
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                itemsIndexed(questions) { index, item ->
                    CreateQuestionCard(index, item)
                }
                item {
                    OutlinedButton(
                        onClick = {
                            viewModel.onQuestionEvent(CreateQuestionEvent.QuestionAdded)
                        },
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .padding(horizontal = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add another question"
                        )
                        Text(text = "Add Question")
                    }
                }
                item {
                    Box(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}