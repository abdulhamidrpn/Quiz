package com.education.ekagratagkquiz.main.presentation.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.NavRoutes
import com.education.ekagratagkquiz.core.util.admob.AdmobBanner
import com.education.ekagratagkquiz.main.data.parcelable.toParcelable
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.presentation.HomeViewModel
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import com.education.ekagratagkquiz.main.util.QuizInteractionEvents

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllQuizList(
    quizzes: List<QuizModel?>,
    navController: NavController,
    showDialog: Boolean,
    onUnselect: () -> Unit,
    modifier: Modifier = Modifier,
    arrangementStyle: QuizArrangementStyle = QuizArrangementStyle.GridStyle,
    selectedQuiz: QuizModel? = null,
    viewModel: HomeViewModel = viewModel(),
    isAdmin: State<Boolean>
) {
    val store = UserStore(LocalContext.current)
    val isSubscriptionActive = store.isSubscriptionActive.collectAsState(initial = false)

    if (selectedQuiz != null && showDialog && !isAdmin.value) {
        AlertDialog(
            onDismissRequest = onUnselect,
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(QuizInteractionEvents.QuizUnselect)
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            NavParams.QUIZ_TAG,
                            selectedQuiz.toParcelable()
                        )
                        navController.navigate(NavRoutes.NavQuizRoute.route + "/${selectedQuiz.uid}")
                    }
                ) {
                    Text(text = "Start now")
                }
            },
            dismissButton = {
                TextButton(onClick = onUnselect) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.secondary)
                }
            },
            title = { Text(text = selectedQuiz.subject) },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.start_quiz_info),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    if (!selectedQuiz.pdf.isNullOrEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(QuizInteractionEvents.QuizUnselect)
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    NavParams.QUIZ_TAG,
                                    selectedQuiz.toParcelable()
                                )
                                navController.navigate(NavRoutes.NavPreparationRoute.route + "/${selectedQuiz.uid}")
                            }
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Take a quick preparation",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                                Spacer(modifier = Modifier.weight(1f)) // Optional spacer for better icon positioning
                                Icon(
                                    imageVector = Icons.Filled.ArrowOutward,
                                    contentDescription = "Take preparation"
                                )
                            }
                        }
                    }
                }

            },
        )
    }

    when (arrangementStyle) {
        QuizArrangementStyle.GridStyle -> {
            LazyVerticalStaggeredGrid(
                modifier = modifier.padding(4.dp),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalItemSpacing = 4.dp,
            ) {
                items(quizzes.size) { index ->
                    quizzes[index]?.let { quiz ->
                        QuizCard(
                            quiz = quiz,
                            arrangement = arrangementStyle,
                            onClick = {
                                if (quiz.isSection) {
                                    /*Go to next section if this is section*/
                                    navController
                                        .currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(NavParams.QUIZ_TAG, quiz.toParcelable())
                                    navController.navigate(NavRoutes.NavDetailsRoute.route + "/${quiz.uid}")
                                } else if (isAdmin.value) {
                                    /*Goto create question page if you are admin*/
                                    navController
                                        .currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(NavParams.QUIZ_TAG, quiz.toParcelable())
                                    navController.navigate(NavRoutes.NavViewQuestions.route + "/${quiz.uid}")
                                } else {
                                    /*Goto Quiz section to perform your quiz*/
                                    viewModel.onEvent(QuizInteractionEvents.QuizSelected(quiz))
                                }

                            },
                            onLongClick = {
                                if (isAdmin.value) {
                                    /*Show Delete option*/
                                    viewModel.onDeleteQuizEvent(
                                        DeleteQuizEvents.PickQuiz(
                                            quizId = quiz.uid,
                                            quizPath = quiz.path
                                        )
                                    )
                                } else {
                                    // TODO: Add to Favourite
                                }
                            }
                        )
                    }
                }

                item {
                    AdmobBanner(custom = true, isSubscriptionActive = isSubscriptionActive.value)
                }
            }
        }

        QuizArrangementStyle.ListStyle -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(quizzes.size) { index ->
                    quizzes[index]?.let { quiz ->
                        QuizCard(
                            quiz = quiz,
                            arrangement = arrangementStyle,
                            onClick = {
                                if (quiz.isSection) {
                                    navController
                                        .currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(NavParams.QUIZ_TAG, quiz.toParcelable())
                                    navController.navigate(NavRoutes.NavDetailsRoute.route + "/${quiz.uid}")
                                } else {
                                    viewModel.onEvent(QuizInteractionEvents.QuizSelected(quiz))
                                }
                            },
                            onLongClick = {

                            }
                        )
                    }
                }

                item {
                    AdmobBanner(
                        modifier = Modifier.fillMaxWidth(),
                        isSubscriptionActive = isSubscriptionActive.value)
                }
            }
        }

        else -> {}
    }
}
