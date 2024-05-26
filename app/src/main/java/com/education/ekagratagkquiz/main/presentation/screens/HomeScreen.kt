package com.education.ekagratagkquiz.main.presentation.screens

import android.content.Context
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.NavRoutes
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.presentation.HomeViewModel
import com.education.ekagratagkquiz.main.presentation.composables.AllQuizList
import com.education.ekagratagkquiz.main.presentation.composables.HomeTabTitleBar
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import com.education.ekagratagkquiz.main.util.QuizInteractionEvents
import com.education.ekagratagkquiz.profile.presentation.ChangeNameEvent
import com.education.ekagratagkquiz.profile.presentation.UserProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    isExpandedScreen: Boolean = false
) {
    Log.d("TAG", "HomeScreen: isExpandedScreen: $isExpandedScreen")

    val store = UserStore(context)
    val isAdmin = store.isAdmin.collectAsState(initial = false)
    val userName = store.getUserName.collectAsState(initial = "")
    val snackBarHostState = remember { SnackbarHostState() }
    /*LaunchedEffect(viewModel) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
                UiEvent.NavigateBack -> {
                    val quizId = "navigateBack"
                    val route =
                        NavRoutes.NavDetailsRoute.route + "/$quizId" + "?${NavParams.SOURCE_VALID_ID}=false"
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        NavParams.QUIZ_TAG,
                        null
                    )
                    navController.navigate(route)
                }
                else -> {}
            }
        }
    }*/


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {

            if (isAdmin.value) {

                ExtendedFloatingActionButton(
                    onClick = {

                        val quizParcelable = QuizParcelable(uid = "unknown")
                        navController
                            .currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(NavParams.QUIZ_TAG, quizParcelable)
                        navController.navigate(NavRoutes.NavCreateQuizRoute.route + "/${quizParcelable.uid}")

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


        if (viewModel.deleteQuizState.value.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeleteQuizEvent(DeleteQuizEvents.OnDeleteCanceled) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteQuizEvent(
                                DeleteQuizEvents.OnDeleteConfirmed(
                                    quizPath = viewModel.deleteQuizState.value.quizPath
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
                        if (viewModel.deleteQuizState.value.isDeleting) {
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

        if (userName.value.isEmpty()) {
            val state = userProfileViewModel.userNameState.value
            AlertDialog(
                onDismissRequest = { userProfileViewModel.onChangeNameEvent(ChangeNameEvent.ToggleDialog) },
                confirmButton = {
                    Button(onClick = { userProfileViewModel.onChangeNameEvent(ChangeNameEvent.SubmitRequest) })
                    { Text(text = "Change") }
                },
                dismissButton = {
                    TextButton(onClick = { userProfileViewModel.onChangeNameEvent(ChangeNameEvent.ToggleDialog) })
                    { Text(text = "Cancel") }
                },
                title = { Text(text = "Change UserName") },
                text = {
                    Column(
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        TextField(
                            value = state.name,
                            isError = state.error != null,
                            onValueChange = {
                                userProfileViewModel.onChangeNameEvent(
                                    ChangeNameEvent.NameChanged(
                                        it
                                    )
                                )
                            },
                            placeholder = { Text(text = "New username") },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = TextFieldDefaults.textFieldColors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                errorIndicatorColor = Color.Transparent,
                            ),
                            shape = MaterialTheme.shapes.medium,
                        )
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .height(16.dp)
                        ) {
                            state.error?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            )
        }

        if (isExpandedScreen) {
            /*View for landscape*/
            HomeScreenExpanded(
                navController = navController,
                padding = padding,
                isAdmin = isAdmin,
                store = store,
            )
        } else {
            /*View for Portrait Compact*/
            HomeScreenCompact(
                navController = navController,
                padding = padding,
                isAdmin = isAdmin,
                store = store,
            )
        }

    }
}


@Composable
fun HomeScreenCompact(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    padding: PaddingValues,
    isAdmin: State<Boolean>,
    store: UserStore
) {
    Column(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .padding(padding)
            .fillMaxSize()
    ) {
        HomeTabTitleBar(
            title = "Your Quizzes",
            arrangementStyle = viewModel.arrangementStyle.value,
            onListStyle = { viewModel.onArrangementChange(QuizArrangementStyle.ListStyle) },
            onGridStyle = { viewModel.onArrangementChange(QuizArrangementStyle.GridStyle) }
        )


        // TODO: Uncomment for only admin
        /*Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =
                if (isAdmin.value) {
                    "View as user"
                } else {
                    "Want to edit"
                }
            )
            Switch(
                checked = isAdmin.value,
                onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        store.saveIsAdmin(!isAdmin.value)
                    }
                }
            )
        }*/

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
fun HomeScreenExpanded(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    padding: PaddingValues,
    isAdmin: State<Boolean>,
    store: UserStore
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


            // TODO: Uncomment for only admin
            /*Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                    if (isAdmin.value) {
                        "View as user"
                    } else {
                        "Want to edit"
                    }
                )
                Switch(
                    checked = isAdmin.value,
                    onCheckedChange = {
                        CoroutineScope(Dispatchers.IO).launch {
                            store.saveIsAdmin(!isAdmin.value)
                        }
                    }
                )
            }*/

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