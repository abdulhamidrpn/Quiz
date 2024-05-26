package com.education.ekagratagkquiz.main.presentation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.core.composables.NoContentPlaceHolder
import com.education.ekagratagkquiz.core.composables.QuizInfoParcelable
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.NavRoutes
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.core.util.admob.loadRewardedAd
import com.education.ekagratagkquiz.core.util.admob.showRewardedAd
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.data.parcelable.toParcelable
import com.education.ekagratagkquiz.main.presentation.composables.FinalQuizInfoExtra
import com.education.ekagratagkquiz.main.presentation.composables.InterActiveQuizCard
import com.education.ekagratagkquiz.main.presentation.composables.LoadingDialog
import com.education.ekagratagkquiz.main.presentation.composables.result.CheckTopperListCard
import com.education.ekagratagkquiz.main.presentation.composables.result.NonInterActiveQuizResultCard
import com.education.ekagratagkquiz.main.presentation.composables.result.QuizResultsCard
import com.education.ekagratagkquiz.main.util.FinalQuizEvent
import com.education.ekagratagkquiz.main.util.FullQuizState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentQuizRoute(
    navController: NavController,
    modifier: Modifier = Modifier,
    parcelable: QuizParcelable? = null,
    isBackHandlerEnabled: Boolean,
    fullQuizState: FullQuizState,
    viewModel: FullQuizViewModel = hiltViewModel(),
    isExpandedScreen: Boolean = false
) {
    val store = UserStore(LocalContext.current)
    val isSubscriptionActive = store.isSubscriptionActive.collectAsState(initial = false)

    val backMessage = stringResource(id = R.string.back_not_allowed)

    val context = LocalContext.current
    BackHandler(
        enabled = isBackHandlerEnabled,
        onBack = { viewModel.onBackClicked(backMessage) }
    )

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        loadRewardedAd(context)
        viewModel.infoMessages.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
                is UiEvent.NavigateBack -> navController.navigateUp()
                is UiEvent.NavigateResultView -> navController.navigateUp()
                else -> {}
            }
        }
    }
    LaunchedEffect(viewModel.fullQuizState.value.isAdView) {
        Log.d(
            "TAGAD",
            "CurrentQuizRoute: launched effect isAdView = ${viewModel.fullQuizState.value.isAdView}" +
                    "\nisSubscriptionActive ${isSubscriptionActive.value}"
        )
        if (viewModel.fullQuizState.value.isAdView) {
            if (isSubscriptionActive.value) {
                viewModel.showResultView()
            } else {
                showRewardedAd(
                    context,
                    isSubscriptionActive = isSubscriptionActive.value,
                    onAdDismissed = {
                        viewModel.showResultView()
                    })
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            SmallTopAppBar(
                title = { Text(text = if (viewModel.fullQuizState.value.isQuizResultView) "Result" else "Start Quiz") },
                navigationIcon = {
                    if (navController.currentBackStackEntry != null && !isBackHandlerEnabled)
                        IconButton(
                            onClick = { navController.navigateUp() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back Button"
                            )
                        }
                },
            )
        },
        floatingActionButton = {
            if (fullQuizState.questions.isNotEmpty() && !fullQuizState.isQuestionLoading) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (viewModel.fullQuizState.value.isQuizResultView) {
                            viewModel.onBackClicked("")
                        } else {
                            viewModel.onOptionEvent(FinalQuizEvent.SubmitQuiz)
                        }
                    }

                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "Submitting")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (viewModel.fullQuizState.value.isQuizResultView) "Done"
                        else "Submit"
                    )
                }
            }
        }
    ) { padding ->


        if (isExpandedScreen) {
            /*View for landscape*/
            QuizScreenExpanded(
                navController = navController,
                padding = padding,
                modifier = modifier,
                parcelable = parcelable,
                fullQuizState = fullQuizState,
                viewModel = viewModel
            )
        } else {
            /*View for Portrait Compact*/
            QuizScreenCompact(
                navController = navController,
                padding = padding,
                modifier = modifier,
                parcelable = parcelable,
                fullQuizState = fullQuizState,
                viewModel = viewModel
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenCompact(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navController: NavController,
    parcelable: QuizParcelable? = null,
    fullQuizState: FullQuizState,
    viewModel: FullQuizViewModel = hiltViewModel()
) {

    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        parcelable?.let { QuizInfoParcelable(quiz = it, showId = false) }
        if (fullQuizState.isLoading)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = { CircularProgressIndicator() }
            )
        else if (fullQuizState.isResultViewLoading)
            LoadingDialog(title = "Calculating your result.")
        else if (fullQuizState.quiz != null) {
            Log.d("TAG", "setResult: fullquizstate ${fullQuizState.quiz}")
            QuizInfoParcelable(
                quiz = fullQuizState.quiz.toParcelable(),
                showId = false
            )
        } else if (fullQuizState.isQuestionLoading)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = { CircularProgressIndicator() }
            )
        else if (fullQuizState.questions.isEmpty())
            NoContentPlaceHolder(
                primaryText = "Nothing Found",
                imageRes = R.drawable.confused,
                secondaryText = "Seems it's mistakenly got approved.Quiz with zero questions aren't possible.Sorry for the mistake",
                graphicsLayer = {
                    rotationX = 12.5f
                }
            )
        else {

            if (!viewModel.fullQuizState.value.isQuizResultView) {
                FinalQuizInfoExtra(
                    attempted = viewModel.quizState.value.attemptedCount,
                    content = fullQuizState.questions,
                )
            }
            Divider(
                modifier = Modifier
                    .padding(PaddingValues(top = 4.dp))
                    .height(2.dp),
                color = MaterialTheme.colorScheme.secondary
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                item {

                    if (viewModel.fullQuizState.value.isQuizResultView && viewModel.fullQuizState.value.quizResult != null) {
                        QuizResultsCard(
                            result = viewModel.fullQuizState.value.quizResult!!,
                            onDelete = {}
                        )
                        CheckTopperListCard(
                            onToggle = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    NavParams.QUIZ_TAG,
                                    parcelable
                                )
                                navController.navigate(
                                    NavRoutes.NavResultsRoute.route + "/" +
                                            "${parcelable?.uid}"
                                )
                            }
                        )
                        Divider(
                            modifier = Modifier
                                .padding(PaddingValues(top = 4.dp))
                                .height(2.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                itemsIndexed(fullQuizState.questions) { idx, item ->
                    item?.let {
                        /*Show performed result in the same list.*/
                        if (viewModel.fullQuizState.value.isQuizResultView) {
                            NonInterActiveQuizResultCard(
                                optionState = viewModel.quizState.value.optionsState,
                                quiz = item,
                                quizIndex = idx,
                            )
                        } else {
                            /*Perform Quiz*/
                            InterActiveQuizCard(optionState = viewModel.quizState.value.optionsState,
                                quiz = item,
                                quizIndex = idx,
                                onUnpick = {
                                    viewModel.onOptionEvent(FinalQuizEvent.OptionUnpicked(idx))
                                },
                                onPick = { option ->
                                    viewModel.onOptionEvent(
                                        FinalQuizEvent.OptionPicked(idx, option, item)
                                    )
                                },
                                onInputAnswer = { option ->
                                    viewModel.onOptionEvent(
                                        FinalQuizEvent.OptionInputAnswer(idx, option)
                                    )
                                }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenExpanded(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navController: NavController,
    parcelable: QuizParcelable? = null,
    fullQuizState: FullQuizState,
    viewModel: FullQuizViewModel = hiltViewModel()
) {

    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        if (fullQuizState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = { CircularProgressIndicator() }
            )
        } else if (fullQuizState.isResultViewLoading) {
            LoadingDialog(title = "Calculating your result.")
        } else if (fullQuizState.quiz != null) {
            Log.d("TAG", "setResult: fullquizstate ${fullQuizState.quiz}")
            QuizInfoParcelable(
                quiz = fullQuizState.quiz.toParcelable(),
                showId = false
            )
        } else if (fullQuizState.isQuestionLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = { CircularProgressIndicator() }
            )
        } else if (fullQuizState.questions.isEmpty()) {
            NoContentPlaceHolder(
                primaryText = "Nothing Found",
                imageRes = R.drawable.confused,
                secondaryText = "Seems it's mistakenly got approved.Quiz with zero questions aren't possible.Sorry for the mistake",
                graphicsLayer = {
                    rotationX = 12.5f
                }
            )
        } else {

            val scrollState = rememberScrollState()
            Row(
                modifier = modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
            ) {

                Column(
                    modifier = modifier
                        .padding(horizontal = 10.dp)
                        .weight(0.4f)
                        .verticalScroll(scrollState)
                ) {

                    parcelable?.let { QuizInfoParcelable(quiz = it, showId = false) }
                    if (!viewModel.fullQuizState.value.isQuizResultView) {
                        FinalQuizInfoExtra(
                            attempted = viewModel.quizState.value.attemptedCount,
                            content = fullQuizState.questions,
                        )
                    }

                    if (viewModel.fullQuizState.value.isQuizResultView && viewModel.fullQuizState.value.quizResult != null) {
                        QuizResultsCard(
                            result = viewModel.fullQuizState.value.quizResult!!,
                            onDelete = {}
                        )
                        CheckTopperListCard(
                            onToggle = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    NavParams.QUIZ_TAG,
                                    parcelable
                                )
                                navController.navigate(
                                    NavRoutes.NavResultsRoute.route + "/" +
                                            "${parcelable?.uid}"
                                )
                            }
                        )
                        Divider(
                            modifier = Modifier
                                .padding(PaddingValues(top = 4.dp))
                                .height(2.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .padding(PaddingValues(top = 4.dp))
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }


                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.6f)
                ) {
                    itemsIndexed(fullQuizState.questions) { idx, item ->
                        item?.let {
                            /*Show performed result in the same list.*/
                            if (viewModel.fullQuizState.value.isQuizResultView) {
                                NonInterActiveQuizResultCard(
                                    optionState = viewModel.quizState.value.optionsState,
                                    quiz = item,
                                    quizIndex = idx,
                                )
                            } else {
                                /*Perform Quiz*/
                                InterActiveQuizCard(optionState = viewModel.quizState.value.optionsState,
                                    quiz = item,
                                    quizIndex = idx,
                                    onUnpick = {
                                        viewModel.onOptionEvent(FinalQuizEvent.OptionUnpicked(idx))
                                    },
                                    onPick = { option ->
                                        viewModel.onOptionEvent(
                                            FinalQuizEvent.OptionPicked(idx, option, item)
                                        )
                                    },
                                    onInputAnswer = { option ->
                                        viewModel.onOptionEvent(
                                            FinalQuizEvent.OptionInputAnswer(idx, option)
                                        )
                                    }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}