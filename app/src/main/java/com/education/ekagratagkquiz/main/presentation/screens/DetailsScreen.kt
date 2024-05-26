package com.education.ekagratagkquiz.main.presentation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteWholeQuizState
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.NavRoutes
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.presentation.HomeViewModel
import com.education.ekagratagkquiz.main.presentation.composables.AllQuizList
import com.education.ekagratagkquiz.main.presentation.composables.HomeTabTitleBar
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import com.education.ekagratagkquiz.main.util.QuizInteractionEvents
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    quizParcelable: QuizParcelable? = null,
    navController: NavController,
    deleteQuizState: DeleteWholeQuizState,
    viewModel: HomeViewModel = hiltViewModel(),
    isAdmin: State<Boolean>,
    isExpandedScreen: Boolean = false
) {

    val TAG = "DetailsScreen"
    val snackBarState = remember { SnackbarHostState() }

//    viewModel.getAllQuizzes(quizParcelable)

    LaunchedEffect(key1 = quizParcelable) { // Refetch on content change
        Log.d(TAG, "DetailsScreen: quizParcelable")
        viewModel.getAllQuizzes(quizParcelable)
    }

    /*LaunchedEffect(key1 = viewModel.reloadTrigger) { // Observe reload events
        Log.d(TAG, "DetailsScreen: reloadTrigger")
        viewModel.reloadTrigger.collect {
            Log.d(TAG, "DetailsScreen: reloadTrigger collected")
            viewModel.getAllQuizzes(quizParcelable) // Refetch data on reload
        }
    }*/

    LaunchedEffect(viewModel) {
        Log.d(TAG, "DetailsScreen: launch effect viewmodel")

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
                title = {
                    Text(
                        text = quizParcelable?.subject ?: "Unknown"
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(
                            onClick = navController::navigateUp
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back button"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {

            if (isAdmin.value) {

                ExtendedFloatingActionButton(
                    onClick = {
                        navController
                            .currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(NavParams.QUIZ_TAG, quizParcelable)
                        navController.navigate(NavRoutes.NavCreateQuizRoute.route + "/${quizParcelable?.uid}")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Create section or quiz"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Create")
                }

            }
        }
    ) { padding ->

        if (deleteQuizState.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeleteQuizEvent(DeleteQuizEvents.OnDeleteCanceled) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteQuizEvent(
                                DeleteQuizEvents.OnDeleteConfirmed(
                                    quizPath = quizParcelable?.path
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

        DetailsScreenCompact(
            navController = navController,
            padding = padding,
            isAdmin = isAdmin
        )


        /*if (isExpandedScreen) {
            *//*View for landscape*//*
            DetailsScreenExpanded(
                navController = navController,
                padding = padding,
                isAdmin = isAdmin
            )
        } else {
            *//*View for Portrait Compact*//*
            DetailsScreenCompact(
                navController = navController,
                padding = padding,
                isAdmin = isAdmin
            )
        }*/


    }
}


@Composable
fun DetailsScreenCompact(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    padding: PaddingValues,
    isAdmin: State<Boolean>
) {
    Column(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .padding(padding)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val quizContent = viewModel.quizzes.value
            if (quizContent.isLoading)
                CircularProgressIndicator()
            else if (quizContent.content?.isNotEmpty() == true) {
                AllQuizList(
                    quizzes = quizContent.content,
                    navController = navController,
                    arrangementStyle = viewModel.arrangementStyle.value,
                    modifier = Modifier.fillMaxSize(),
                    onUnselect = { viewModel.onEvent(QuizInteractionEvents.QuizUnselect) },
                    showDialog = viewModel.showDialog.value,
                    selectedQuiz = viewModel.selectedQuiz.value,
                    isAdmin = isAdmin
                )
            } else Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.quiz),
                    contentDescription = "No contribution",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No quizzes are available",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "No quizzes are added or none of them are  approved",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}


@Composable
fun DetailsScreenExpanded(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    padding: PaddingValues,
    isAdmin: State<Boolean>
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .padding(padding)
            .fillMaxSize()
    ) {

        Column(
            modifier = modifier
                .padding(horizontal = 10.dp)
                .padding(padding)
                .weight(0.3f)
                .verticalScroll(scrollState)
        ) {
            HomeTabTitleBar(
                title = "Your Quizzes",
                arrangementStyle = viewModel.arrangementStyle.value,
                onListStyle = { viewModel.onArrangementChange(QuizArrangementStyle.ListStyle) },
                onGridStyle = { viewModel.onArrangementChange(QuizArrangementStyle.GridStyle) }
            )


            Text(
                text = stringResource(id = R.string.quiz_tab_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Note* ")
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                    append(stringResource(id = R.string.all_quizzes_extra_info))
                }
            }, style = MaterialTheme.typography.bodySmall)

        }

        Column(
            modifier = modifier
                .padding(horizontal = 10.dp)
                .padding(padding)
                .weight(0.7f)
        ) {


            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val quizContent = viewModel.quizzes.value
                if (quizContent.isLoading)
                    CircularProgressIndicator()
                else if (quizContent.content?.isNotEmpty() == true) {
                    AllQuizList(
                        quizzes = quizContent.content,
                        navController = navController,
                        arrangementStyle = viewModel.arrangementStyle.value,
                        modifier = Modifier.fillMaxSize(),
                        onUnselect = { viewModel.onEvent(QuizInteractionEvents.QuizUnselect) },
                        showDialog = viewModel.showDialog.value,
                        selectedQuiz = viewModel.selectedQuiz.value,
                        isAdmin = isAdmin
                    )
                } else Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.quiz),
                        contentDescription = "No contribution",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No quizzes are available",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "No quizzes are added or none of them are  approved",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }


        }
    }
}