package com.education.ekagratagkquiz.contribute_quiz.presentation.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.presentation.QuizViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.composables.QuizColorPicker
import com.education.ekagratagkquiz.contribute_quiz.presentation.composables.QuizImagePicker
import com.education.ekagratagkquiz.contribute_quiz.presentation.composables.QuizPdfPicker
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuizState
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuiz(
    modifier: Modifier = Modifier,
    navController: NavController,
    state: CreateQuizState,
    showDialog: Boolean,
    parcelable: QuizParcelable? = null,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val snackBarState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messagesFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> snackBarState.showSnackbar(event.message)
                else -> {}
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text =
                        if (viewModel.sectionId.isNotEmpty()) {
                            if (viewModel.sectionId == "new") {
                                "Create Section"
                            } else {
                                "Edit Section"
                            }
                        } else if (viewModel.quizId.isNotEmpty()) {
                            if (viewModel.quizId == "new") {
                                "Create Quiz"
                            } else {
                                "Edit Quiz"
                            }
                        } else {
                            "Edit"
                        }
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(
                            onClick = navController::navigateUp
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    }
                })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onCreateQuizEvent(CreateQuizEvents.OnSubmit(parcelable)) }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Quiz")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add")
            }
        }
    ) { padding ->
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(onClick = {
                        navController.navigateUp()
                    }) { Text(text = "Ok Got it ", style = MaterialTheme.typography.titleMedium) }
                },
                title = {
                    Text(
                        text = "Quiz Added Successfully",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.create_quiz_desc),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                })
        }
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .padding(horizontal = 10.dp)
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val quizSize = parcelable?.quizSize ?: 0L
                if (quizSize > 0L) {
                    /*It should not contain any more section*/
                    viewModel.onCreateQuizEvent(CreateQuizEvents.OnUpdatedToSection(isSection = false))
                }
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            if (state.isSection) {
                                append("Create Section")
                            } else {
                                append("Create Quiz: ")
                            }
                        }
                        withStyle(
                            style = SpanStyle(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            if (!state.isSection) {
                                append("Total Quizzes ${parcelable?.quizSize}")
                            }
                        }
                    }
                )
                Switch(
                    checked = state.isSection,
                    onCheckedChange = {
                        if (quizSize > 0L) {
                            /*It should not contain any more section*/
                            viewModel.onCreateQuizEvent(
                                CreateQuizEvents.OnUpdatedToSection(
                                    isSection = false
                                )
                            )
                        } else {
                            viewModel.onCreateQuizEvent(CreateQuizEvents.OnUpdatedToSection(it))
                        }
                    }
                )
            }
            Text(
                text =
                if (state.isSection) {
                    "Organize your section list"
                } else {
                    "Organize your quiz list"
                },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.create_quiz_desc),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = state.subject,
                onValueChange = { sub ->
                    viewModel.onCreateQuizEvent(CreateQuizEvents.OnSubjectChanges(sub))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        shape = MaterialTheme.shapes.medium,
                        color = if (state.subjectError != null) MaterialTheme.colorScheme.error
                        else Color.Transparent
                    ),
                placeholder = {
                    Text(text = "Untitled", style = MaterialTheme.typography.headlineSmall)
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text
                ),
                textStyle = MaterialTheme.typography.headlineSmall,
                maxLines = 4,
                isError = state.subjectError != null,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium
            )
            state.subjectError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = state.desc,
                onValueChange = { desc ->
                    viewModel.onCreateQuizEvent(
                        CreateQuizEvents.ObDescChange(
                            desc
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        shape = MaterialTheme.shapes.medium,
                        color = if (state.descError != null) MaterialTheme.colorScheme.error
                        else Color.Transparent
                    ),
                placeholder = { Text(text = "Some description") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text
                ),
                maxLines = 10,
                isError = state.descError != null,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium
            )
            state.descError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (!state.isSection) {
                QuizPdfPicker()
            }

            QuizImagePicker()
            QuizColorPicker()
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Normal,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize
                        )
                    ) {
                        append("Note* ")
                    }
                    append(stringResource(id = R.string.create_quiz_info))
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontStyle = FontStyle.Italic,
            )

            Spacer(modifier = Modifier.height(100.dp)) // Add some extra space for potential scrolling

        }
    }
}