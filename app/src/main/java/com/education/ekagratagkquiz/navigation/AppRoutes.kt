package com.education.ekagratagkquiz.navigation

import android.content.Context
import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.education.ekagratagkquiz.contribute_quiz.presentation.CreateQuestionViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.QuestionsViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.QuizViewModel
import com.education.ekagratagkquiz.contribute_quiz.presentation.screens.ContributedQuestions
import com.education.ekagratagkquiz.contribute_quiz.presentation.screens.CreateQuestions
import com.education.ekagratagkquiz.contribute_quiz.presentation.screens.CreateQuiz
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.NavRoutes
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.presentation.CurrentQuizRoute
import com.education.ekagratagkquiz.main.presentation.FullQuizViewModel
import com.education.ekagratagkquiz.main.presentation.HomeViewModel
import com.education.ekagratagkquiz.main.presentation.MainRoute
import com.education.ekagratagkquiz.main.presentation.PreparationViewModel
import com.education.ekagratagkquiz.main.presentation.ResultsScreenViewModel
import com.education.ekagratagkquiz.main.presentation.screens.DetailsScreen
import com.education.ekagratagkquiz.main.presentation.screens.PreparationScreen
import com.education.ekagratagkquiz.main.presentation.screens.ResultsScreen

@Composable
fun AppRoutes(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    widthSizeClass: WindowWidthSizeClass
) {
    val TAG = "AppRoutes"
    val navHost = rememberNavController()

    val store = UserStore(context)
    val isAdmin = store.isAdmin.collectAsState(initial = false)

    val isExpandedScreen = (widthSizeClass == WindowWidthSizeClass.Expanded || widthSizeClass == WindowWidthSizeClass.Medium)

    NavHost(
        navController = navHost,
        modifier = modifier,
        startDestination = NavRoutes.NavHomeRoute.route
    ) {
        composable(NavRoutes.NavHomeRoute.route) {
            MainRoute(navController = navHost,widthSizeClass = widthSizeClass)
        }


        composable(
            NavRoutes.NavCreateQuizRoute.route + NavParams.QUIZ_PARAM_ID,
            arguments = listOf(navArgument(NavParams.QUIZ_ID) {
                type = NavType.StringType
            })
        ) { navStack ->

            val viewModel = hiltViewModel<QuizViewModel>()

            val quiz =
                navHost
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<QuizParcelable>(NavParams.QUIZ_TAG)

            CreateQuiz(
                navController = navHost,
                state = viewModel.createQuiz.value,
                parcelable = quiz,
                showDialog = viewModel.quizDialogState.value
            )
        }

        composable(
            NavRoutes.NavDetailsRoute.route + NavParams.QUIZ_PARAM_ID,
            arguments = listOf(navArgument(NavParams.QUIZ_ID) {
                type = NavType.StringType
            })
        ) { navStack ->
            val quizId =
                navStack.arguments?.getString(NavParams.QUIZ_ID) ?: ""
            val quiz =
                navHost
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<QuizParcelable>(NavParams.QUIZ_TAG)?.copy(uid = quizId)


            val viewModel = hiltViewModel<HomeViewModel>()
            Log.d("TAG", "AllQuizList: quiz approutes uid ${quiz?.uid}")
            Log.d("TAG", "AllQuizList: quiz approutes id  ${quizId}")


            DetailsScreen(
                navController = navHost,
                quizParcelable = quiz,
                isAdmin = isAdmin,
                viewModel = viewModel,
                deleteQuizState = viewModel.deleteQuizState.value,
                isExpandedScreen = isExpandedScreen
            )
        }


        composable(
            NavRoutes.NavQuizRoute.route + NavParams.QUIZ_PARAM_ID + NavParams.SOURCE_VALID_PARAMS_ID,
            arguments = listOf(
                navArgument(NavParams.QUIZ_ID) { type = NavType.StringType },
                navArgument(NavParams.SOURCE_VALID_ID) {
                    type = NavType.BoolType; defaultValue = true
                }
            )
        ) {
            val parcelable = navHost
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<QuizParcelable>(NavParams.QUIZ_TAG)

            val viewModel = hiltViewModel<FullQuizViewModel>()

            Log.d(TAG, "AppRoutes: quizId is ${NavParams.QUIZ_ID}")
            Log.d(TAG, "AppRoutes: quiz parcel is ${parcelable.toString()}")
            viewModel.setQuiz(parcelable)
            CurrentQuizRoute(
                navController = navHost,
                parcelable = parcelable,
                isBackHandlerEnabled = viewModel.routeState.value.isBackNotAllowed,
                fullQuizState = viewModel.fullQuizState.value,
                isExpandedScreen = isExpandedScreen
            )
        }

        composable(
            NavRoutes.NavResultsRoute.route + NavParams.QUIZ_PARAM_ID + NavParams.SOURCE_VALID_PARAMS_ID,
            arguments = listOf(
                navArgument(NavParams.QUIZ_ID) { type = NavType.StringType },
                navArgument(NavParams.SOURCE_VALID_ID) {
                    type = NavType.BoolType; defaultValue = true
                }
            )
        ) {
            val parcelable = navHost
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<QuizParcelable>(NavParams.QUIZ_TAG)

            val viewModel = hiltViewModel<ResultsScreenViewModel>()

            ResultsScreen(
                viewModel = viewModel,
                navController = navHost,
                parcelable = parcelable,
                isExpandedScreen = isExpandedScreen
            )
        }

        composable(
            NavRoutes.NavPreparationRoute.route + NavParams.QUIZ_PARAM_ID + NavParams.SOURCE_VALID_PARAMS_ID,
            arguments = listOf(
                navArgument(NavParams.QUIZ_ID) { type = NavType.StringType },
                navArgument(NavParams.SOURCE_VALID_ID) {
                    type = NavType.BoolType; defaultValue = true
                }
            )
        ) {
            val parcelable = navHost
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<QuizParcelable>(NavParams.QUIZ_TAG)

            val viewModel = hiltViewModel<PreparationViewModel>()

            PreparationScreen(
                viewModel = viewModel,
                navController = navHost,
                parcelable = parcelable,
            )
        }

        composable(
            NavRoutes.NavViewQuestions.route + NavParams.QUIZ_PARAM_ID,
            arguments = listOf(navArgument(NavParams.QUIZ_ID) {
                type = NavType.StringType
            })
        ) { navStack ->
            val quizId =
                navStack.arguments?.getString(NavParams.QUIZ_ID) ?: ""
            val quiz =
                navHost
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<QuizParcelable>(NavParams.QUIZ_TAG)
            val viewModel = hiltViewModel<QuestionsViewModel>()

            ContributedQuestions(
                navController = navHost,
                quizId = quizId,
                parcelable = quiz,
                deleteQuizState = viewModel.deleteQuizState.value,
                deleteQuestionState = viewModel.deleteQuestionState.value,
                questionsState = viewModel.questions.value
            )
        }




        composable(
            NavRoutes.NavAddQuestionsRoute.route + NavParams.QUIZ_PARAM_ID,
            arguments = listOf(navArgument(NavParams.QUIZ_ID) {
                type = NavType.StringType
            })
        ) { navStack ->
            val quizId =
                navStack.arguments?.getString(NavParams.QUIZ_ID) ?: ""
            val quiz =
                navHost
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<QuizParcelable>(NavParams.QUIZ_TAG)

            val viewModel = hiltViewModel<CreateQuestionViewModel>()
            CreateQuestions(
                navController = navHost,
                quizId = quizId,
                parcelable = quiz,
                viewModel = viewModel,
                excelQuestionsState = viewModel.excelQuestionsState.value,
            )
        }

    }
}