package com.education.ekagratagkquiz.main.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.core.composables.NoContentPlaceHolder
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.presentation.ResultsScreenViewModel
import com.education.ekagratagkquiz.main.presentation.composables.QuizResultsUniversal
import com.education.ekagratagkquiz.main.util.DeleteQuizResultsEvent
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    parcelable: QuizParcelable? = null,
    viewModel: ResultsScreenViewModel = hiltViewModel(),
    isExpandedScreen: Boolean = false
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val TAG = "ResultsScreen"

    LaunchedEffect(parcelable) {
        Log.d(TAG, "ResultsScreen: quiz ${parcelable?.uid}")
        Log.d(TAG, "ResultsScreen: quiz ${parcelable.toString()}")

        if (parcelable == null) {
            viewModel.getResults()
        } else {
            viewModel.getResults(parcelable.uid)
        }


    }
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
                UiEvent.NavigateBack -> {
                    /*val quizId = viewModel.searchQuizState.value.quizId
                    val route =
                        NavRoutes.NavQuizRoute.route + "/$quizId" + "?${NavParams.SOURCE_VALID_ID}=false"
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        NavParams.QUIZ_TAG,
                        null
                    )
                    navController.navigate(route)*/
                }

                else -> {}
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->

        if (isExpandedScreen) {
            ResultScreenExpanded(
                parcelable, viewModel, padding
            )
        } else {
            ResultScreenCompact(
                parcelable, viewModel, padding
            )
        }

    }
}


@Composable
fun ResultScreenCompact(
    parcelable: QuizParcelable? = null,
    viewModel: ResultsScreenViewModel = hiltViewModel(),
    padding: PaddingValues
) {
    val content = viewModel.content.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = if (parcelable == null) stringResource(R.string.result_topper_desc) else "Results of " + parcelable.subject,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp)
        )
        Text(
            text = if (parcelable == null) stringResource(id = R.string.results_universal_desc) else stringResource(id = R.string.result_topper_sub_desc) ,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )



        if (content.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = { CircularProgressIndicator() }
            )
        } else if (content.content?.isNotEmpty() == true) {
            QuizResultsUniversal(
                content = content.content,
                state = viewModel.deleteQuizState.value,
                onDeleteCancelled = {
                    viewModel.onDeleteResult(
                        DeleteQuizResultsEvent.DeleteCanceled
                    )
                }, onDeleteConfirm = {
                    viewModel.onDeleteResult(DeleteQuizResultsEvent.DeleteConfirmed)
                }
            )
        } else {
            NoContentPlaceHolder(
                imageRes = R.drawable.qualification,
                primaryText = "No Results were found",
                secondaryText = stringResource(id = R.string.results_not_found),
                graphicsLayer = {
                    rotationZ = -12.5f
                }
            )

        }
    }
}

@Composable
fun ResultScreenExpanded(
    parcelable: QuizParcelable? = null,
    viewModel: ResultsScreenViewModel = hiltViewModel(),
    padding: PaddingValues
) {
    val content = viewModel.content.value
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(0.4f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = if (parcelable == null) "The top 3 scorers\nfrom all quizzes." else "Results of " + parcelable.subject,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 26.dp)
            )
            Text(
                text = if (parcelable == null) stringResource(id = R.string.results_universal_desc) else stringResource(id = R.string.result_topper_sub_desc) ,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }


        Column(
            modifier = Modifier
                .weight(0.6f)
                .padding(horizontal = 16.dp)
        ) {
            if (content.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = { CircularProgressIndicator() }
                )
            } else if (content.content?.isNotEmpty() == true) {
                QuizResultsUniversal(
                    content = content.content,
                    state = viewModel.deleteQuizState.value,
                    onDeleteCancelled = {
                        viewModel.onDeleteResult(
                            DeleteQuizResultsEvent.DeleteCanceled
                        )
                    }, onDeleteConfirm = {
                        viewModel.onDeleteResult(DeleteQuizResultsEvent.DeleteConfirmed)
                    }
                )
            } else {
                NoContentPlaceHolder(
                    imageRes = R.drawable.qualification,
                    primaryText = "No Results were found",
                    secondaryText = stringResource(id = R.string.results_not_found),
                    graphicsLayer = {
                        rotationZ = -12.5f
                    }
                )
            }
        }
    }
}


@Composable
fun AdaptiveContainer(
    isExpanded: Boolean, // Flag indicating expanded state
    content: @Composable () -> Unit
) {
    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            content()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            content()
        }
    }
}
