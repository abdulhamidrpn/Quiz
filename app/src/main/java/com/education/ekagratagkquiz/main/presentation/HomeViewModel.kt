package com.education.ekagratagkquiz.main.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuestionsState
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteWholeQuizState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionDeleteEvent
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.ShowContent
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.domain.repository.HomeRepository
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import com.education.ekagratagkquiz.main.util.QuizInteractionEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepository
) : ViewModel() {

    private val TAG = HomeViewModel::class.java.simpleName

    private val messages = MutableSharedFlow<UiEvent>()

    val uiEvents = messages.asSharedFlow()

    private val _reloadTrigger = MutableSharedFlow<Unit>()
    val reloadTrigger = _reloadTrigger.asSharedFlow()

    var showDialog = mutableStateOf(false)
        private set

    var deleteQuizState = mutableStateOf(DeleteWholeQuizState())
        private set

    var selectedQuiz = mutableStateOf<QuizModel?>(null)
        private set

    var quizzes = mutableStateOf<ShowContent<List<QuizModel?>>>(ShowContent(isLoading = true))
        private set

    private val errorMessages = MutableSharedFlow<UiEvent>()
    val errorFlow = errorMessages.asSharedFlow()

    var arrangementStyle = mutableStateOf<QuizArrangementStyle>(QuizArrangementStyle.GridStyle)
        private set

    fun onArrangementChange(event: QuizArrangementStyle) {
        when (event) {
            QuizArrangementStyle.GridStyle -> {
                arrangementStyle.value = event
            }

            QuizArrangementStyle.ListStyle -> {
                arrangementStyle.value = event
            }
        }
    }

    fun onEvent(event: QuizInteractionEvents) {
        when (event) {
            is QuizInteractionEvents.QuizSelected -> {
                Log.d(TAG, "onEvent: QuizSelected ${event.quiz}")

                if (event.quiz.isSection) {
                    selectedQuiz.value = event.quiz
                } else {
                    showDialog.value = true
                    selectedQuiz.value = event.quiz
                }
            }

            QuizInteractionEvents.QuizUnselect -> {
                showDialog.value = false
            }
        }
    }


    fun onDeleteQuizEvent(event: DeleteQuizEvents) {
        when (event) {
            DeleteQuizEvents.OnDeleteCanceled -> {
                deleteQuizState.value = deleteQuizState.value.copy(
                    showDialog = false,
                    quizId = null
                )
            }

            is DeleteQuizEvents.OnDeleteConfirmed -> {
                deleteQuizState.value = deleteQuizState.value.copy(isDeleting = true)
                deleteQuiz(quizPath = deleteQuizState.value.quizPath, quizId = deleteQuizState.value.quizId)
            }

            is DeleteQuizEvents.PickQuiz -> {
                deleteQuizState.value = deleteQuizState.value.copy(
                    showDialog = true,
                    quizId = event.quizId,
                    quizPath = event.quizPath
                )
            }
        }
    }


    private fun deleteQuiz(quizPath: String? = "",quizId:String? = "") {
        // then close the dialog
        viewModelScope.launch {
            repo.deleteQuiz(quizPath, quizId).onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        deleteQuizState.value = deleteQuizState.value.copy(
                            isDeleting = false,
                            showDialog = false,
                            quizId = null
                        )
                        messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                    }

                    is Resource.Loading -> {
                        deleteQuizState.value = deleteQuizState.value.copy(
                            isDeleting = true,
                        )
                    }

                    is Resource.Success -> {
                        deleteQuizState.value = deleteQuizState.value.copy(
                            isDeleting = false,
                            showDialog = false,
                            quizId = null
                        )
                        messages.emit(UiEvent.NavigateBack)
                    }
                }

            }.launchIn(this)
        }
    }

    private fun getAllQuizzes() {
        Log.d(TAG, "getAllQuizzes: Requested queries")
        viewModelScope.launch {

            repo.getAllQuizzes().onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        errorMessages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                        quizzes.value = quizzes.value.copy(isLoading = false, content = null)
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        quizzes.value = quizzes.value.copy(content = res.value, isLoading = false)
                    }
                }
            }.launchIn(this)


            /*/*delay(2000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                quizzes.value = quizzes.value.copy(
                    content = emptyList(), isLoading = false
                )
            }

            delay(4000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                quizzes.value = quizzes.value.copy(
                    content = Constant.quizList, isLoading = false
                )
            }*/*/
        }
    }

    fun getAllQuizzes(quiz: QuizParcelable? = null){
        Log.d(TAG, "getAllQuizzes: Requested queries ${quiz?.path}")
        viewModelScope.launch {

            if (quiz == null) {
                errorMessages.emit(UiEvent.ShowSnackBar("Quiz not found"))
                quizzes.value = quizzes.value.copy(isLoading = false, content = null)
                return@launch
            }

            quizzes.value = ShowContent(isLoading = true)
            repo.getAllQuizzes(quiz).onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        errorMessages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                        quizzes.value = quizzes.value.copy(isLoading = false, content = null)
                    }

                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        quizzes.value = quizzes.value.copy(content = res.value, isLoading = false)
                    }
                }
            }.launchIn(this)


            /*/*delay(2000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                quizzes.value = quizzes.value.copy(
                    content = emptyList(), isLoading = false
                )
            }

            delay(4000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                quizzes.value = quizzes.value.copy(
                    content = Constant.quizList, isLoading = false
                )
            }*/*/
        }
    }


    init {
        getAllQuizzes()
    }
}