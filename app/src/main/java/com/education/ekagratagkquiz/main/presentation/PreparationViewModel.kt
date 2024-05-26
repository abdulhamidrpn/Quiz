package com.education.ekagratagkquiz.main.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizzi.bouquet.HorizontalPdfReaderState
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPdfReaderState
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteWholeQuizState
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.ShowContent
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.domain.models.PreparationModel
import com.education.ekagratagkquiz.main.domain.repository.PreparationRepository
import com.education.ekagratagkquiz.main.util.PreparationEvents
import com.education.ekagratagkquiz.main.util.PreparationInteractionEvents
import com.education.ekagratagkquiz.main.util.PreparationState
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreparationViewModel @Inject constructor(
    private val repo: PreparationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = PreparationViewModel::class.java.simpleName

    private val messages = MutableSharedFlow<UiEvent>()

    val uiEvents = messages.asSharedFlow()

    private val _reloadTrigger = MutableSharedFlow<Unit>()
    val reloadTrigger = _reloadTrigger.asSharedFlow()

    var preparationState = mutableStateOf(PreparationState())
        private set

    var showDialog = mutableStateOf(false)
        private set

    var deleteQuizState = mutableStateOf(DeleteWholeQuizState())
        private set

    var selectedPdfs = mutableStateOf<PreparationModel?>(null)
        private set

    var pdfs = mutableStateOf<ShowContent<List<PreparationModel?>>>(ShowContent(isLoading = true))
        private set




    val pdfVerticalReaderState = VerticalPdfReaderState(
        resource = ResourceType.Remote("https://myreport.altervista.org/Lorem_Ipsum.pdf"),
        isZoomEnable = true
    )

    val pdfHorizontalReaderState = HorizontalPdfReaderState(
        resource = ResourceType.Remote("https://myreport.altervista.org/Lorem_Ipsum.pdf"),
        isZoomEnable = true
    )

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

    fun onPdfSelectEvents(event: PreparationEvents) {
        when (event) {
            is PreparationEvents.readFile -> {
                preparationState.value = preparationState.value.copy(
                    isRequestPermission = false,
                    fileUri = event.uri?.toString()
                )
                // TODO: /*Show file name dialog to enter a subject.*/


                if (event.uri != null) {
                    addFireBasePdf(event.uri)
                }
            }

            PreparationEvents.OnRequestPermission -> {
                preparationState.value = preparationState.value.copy(
                    isRequestPermission = true
                )
            }

            else -> {

            }
        }
    }


    private fun addFireBasePdf(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            messages.emit(UiEvent.ShowSnackBar("Adding this quiz please wait"))


            val pdfLink = repo.uploadPdf(path = uri.toString())
            val preparationModel = PreparationModel(
                path = pdfLink,
            )
            Log.d(TAG, "onPdfSelectEvents: uploadPdf: $pdfLink")
            repo.updatePdf(preparationModel)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            messages.emit(UiEvent.ShowSnackBar(resource.message ?: ""))
                        }

                        is Resource.Success -> {
                            Log.d(TAG, "onPdfSelectEvents: uploadPdf: success")
                        }

                        is Resource.Loading -> messages.emit(UiEvent.ShowSnackBar("Adding this quiz please wait"))
                    }
                }.launchIn(this)
        }
    }


    fun onEvent(event: PreparationInteractionEvents) {
        when (event) {
            is PreparationInteractionEvents.PdfSelected -> {
                selectedPdfs.value = event.preparation
            }

            PreparationInteractionEvents.PdfUnselect -> {
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
                deleteQuiz(
                    quizPath = deleteQuizState.value.quizPath,
                    quizId = deleteQuizState.value.quizId
                )
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


    private fun deleteQuiz(quizPath: String? = "", quizId: String? = "") {
        // then close the dialog
        viewModelScope.launch {
            repo.deletePdf(quizPath, quizId).onEach { res ->
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

            repo.getAllPdfs().onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        errorMessages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                        pdfs.value = pdfs.value.copy(isLoading = false, content = null)
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        pdfs.value = pdfs.value.copy(content = res.value, isLoading = false)
                    }
                }
            }.launchIn(this)

        }
    }


    init {
        getAllQuizzes()
    }
}