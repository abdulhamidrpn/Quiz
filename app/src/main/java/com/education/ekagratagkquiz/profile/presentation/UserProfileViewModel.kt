package com.education.ekagratagkquiz.profile.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.profile.domain.repository.UserProfileRepository
import com.education.ekagratagkquiz.profile.domain.use_cases.UserNameValidatorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val profileRepo: UserProfileRepository,
    val user: FirebaseUser?
) : ViewModel() {

    private val validator = UserNameValidatorUseCase()

    var userNameState = mutableStateOf(UserNameFieldState())

    private val messagesFlow = MutableSharedFlow<UiEvent>()
    val messages = messagesFlow.asSharedFlow()


    fun onChangeNameEvent(event: ChangeNameEvent) {
        when (event) {
            is ChangeNameEvent.NameChanged -> {
                userNameState.value = userNameState.value.copy(
                    name = event.name,
                    error = if (userNameState.value.error != null) null else userNameState.value.error
                )
            }

            ChangeNameEvent.ToggleDialog -> {
                userNameState.value = userNameState.value.copy(
                    isDialogOpen = !userNameState.value.isDialogOpen, isDismissAllowed = true
                )
            }

            ChangeNameEvent.SubmitRequest -> {
                changeUserName()
                userNameState.value = userNameState.value.copy(
                    isDismissAllowed = false, isDialogOpen = false
                )
            }
        }
    }

    private fun changeUserName() {
        val validate = validator.execute(userNameState.value.name)
        if (validate.isValid) {
            viewModelScope.launch {
                when (val newName = profileRepo.updateUserName(userNameState.value.name)) {
                    is Resource.Success -> {
                        messagesFlow.emit(UiEvent.ShowSnackBar("Your name has been updated"))
                    }

                    is Resource.Error -> {
                        messagesFlow.emit(UiEvent.ShowSnackBar(newName.message ?: "Errors"))
                    }

                    is Resource.Loading -> {
                        messagesFlow.emit(UiEvent.ShowSnackBar("Changing username ...."))
                    }
                }
            }
            return
        }
        userNameState.value = userNameState.value.copy(
            error = validate.message
        )
    }

}