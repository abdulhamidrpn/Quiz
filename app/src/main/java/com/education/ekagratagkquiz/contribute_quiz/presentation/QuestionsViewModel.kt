package com.education.ekagratagkquiz.contribute_quiz.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.QuestionsRepository
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuestionsState
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteWholeQuizState
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionEvents
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionsState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionDeleteEvent
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.ShowContent
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import javax.inject.Inject

@HiltViewModel
class QuestionsViewModel @Inject constructor(
    private val repo: QuestionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val TAG = QuestionsViewModel::class.java.simpleName
    init {
        val data = savedStateHandle.get<String>(FireStoreCollections.QUIZ_ID_FIELD)
        Log.d(TAG, "QuestionViewModel: $data")
        Log.d(TAG, "QuestionViewModel: ${savedStateHandle.get<QuizParcelable>(NavParams.QUIZ_TAG)}")

        if (data != null) getCurrentQuizQuestions(data)
    }

    private val messages = MutableSharedFlow<UiEvent>()
    val uiEvents = messages.asSharedFlow()


    var deleteQuestionState = mutableStateOf(DeleteQuestionsState())
        private set

    var deleteQuizState = mutableStateOf(DeleteWholeQuizState())
        private set

    var excelQuestionsState = mutableStateOf(ExcelQuestionsState())
        private set

    val excelQuestions = mutableStateOf<ShowContent<List<QuestionModel?>>>(
        ShowContent(
            isLoading = true,
            content = null
        )
    )

    val questions = mutableStateOf<ShowContent<List<QuestionModel?>>>(
        ShowContent(
            isLoading = true,
            content = null
        )
    )

    fun onQuestionDelete(event: QuestionDeleteEvent) {
        when (event) {
            QuestionDeleteEvent.DeleteConfirmed -> {
                deleteQuestionState.value.model?.let { model ->
                    deleteQuestion(model)
                }
                deleteQuestionState.value = deleteQuestionState.value.copy(
                    isDialogOpen = false,
                    model = null
                )
            }

            is QuestionDeleteEvent.QuestionSelected -> {
                deleteQuestionState.value = deleteQuestionState.value.copy(
                    isDialogOpen = true,
                    model = event.model
                )
            }

            QuestionDeleteEvent.CloseDeleteDialog -> {
                deleteQuestionState.value = deleteQuestionState.value.copy(
                    isDialogOpen = false,
                    model = null
                )
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
//actual delete
//                event.quizPath?.let {
//                    deleteQuiz(it)
//                }
                deleteQuizState.value.quizId?.let { deleteAllQuizQuestion(it) }
            }

            is DeleteQuizEvents.PickQuiz -> {
                deleteQuizState.value = deleteQuizState.value.copy(
                    showDialog = true,
                    quizId = event.quizId
                )
            }
        }
    }

    private fun deleteAllQuizQuestion(id: String) {
        // then close the dialog
        viewModelScope.launch {
            repo.deleteQuiz(id).onEach { res ->
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

    private fun getCurrentQuizQuestions(quizId: String) {
        viewModelScope.launch {
            repo.getQuestions(quizId).onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        questions.value = questions.value.copy(isLoading = false, content = null)
                        messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                    }

                    is Resource.Success -> {
                        questions.value = questions.value.copy(
                            content = res.value,
                            isLoading = false
                        )
                    }

                    else -> {}
                }
            }.launchIn(this)
        }
    }

    private fun deleteQuestion(questionModel: QuestionModel) {
        val model = questions.value.content?.find { it == questionModel }
        if (model != null) {
            viewModelScope.launch {
                repo.deleteQuestion(model)
                    .onEach { res ->
                        when (res) {
                            is Resource.Error -> {
                                messages.emit(
                                    UiEvent.ShowSnackBar(
                                        res.message ?: "Cannot remove the question"
                                    )
                                )
                            }

                            is Resource.Success -> {
                                messages.emit(UiEvent.ShowSnackBar("Question ${model.question} has been removed "))
                            }

                            else -> {}
                        }
                    }.launchIn(this)
            }
        }
    }


}