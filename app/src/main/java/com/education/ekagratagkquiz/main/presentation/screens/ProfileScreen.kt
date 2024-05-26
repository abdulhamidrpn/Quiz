package com.education.ekagratagkquiz.main.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.education.ekagratagkquiz.ChooseSubscription
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.profile.presentation.ChangeNameEvent
import com.education.ekagratagkquiz.profile.presentation.UserProfileViewModel
import com.education.ekagratagkquiz.profile.presentation.composables.ChangeUserNameSettings
import com.education.ekagratagkquiz.profile.presentation.composables.ShareAppCard
import com.education.ekagratagkquiz.profile.presentation.composables.SubscriptionCard
import com.education.ekagratagkquiz.profile.presentation.composables.TelegramCard
import com.education.ekagratagkquiz.profile.presentation.composables.UserInfoCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("QueryPermissionsNeeded")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val store = UserStore(context)
    val user = store.getUserDetail.collectAsState(initial = FirebaseUser())

    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    LaunchedEffect(viewModel) {
        viewModel.messages
            .collectLatest { event ->
                when (event) {
                    is UiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.title)
                    else -> {}
                }
            }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->


        Column(
            verticalArrangement = Arrangement.Top,
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 10.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(id = R.string.profile_info),
                style = MaterialTheme.typography.bodyMedium,
            )
            UserInfoCard(
                user = user.value,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            ChangeUserNameSettings(
                state = viewModel.userNameState.value,
                user = viewModel.user,
                onToggle = {
                    viewModel.onChangeNameEvent(ChangeNameEvent.ToggleDialog)
                },
                onSubmit = {
                    viewModel.onChangeNameEvent(ChangeNameEvent.SubmitRequest)
                },
                onChange = {
                    viewModel.onChangeNameEvent(ChangeNameEvent.NameChanged(it))
                }
            )
            SubscriptionCard()
            TelegramCard()
            ShareAppCard()
        }
    }
}