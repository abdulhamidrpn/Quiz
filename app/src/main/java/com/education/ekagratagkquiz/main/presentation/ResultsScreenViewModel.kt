package com.education.ekagratagkquiz.main.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.ShowContent
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import com.education.ekagratagkquiz.main.domain.repository.QuizResultsRepository
import com.education.ekagratagkquiz.main.domain.use_case.DocumentIdValidator
import com.education.ekagratagkquiz.main.util.DeleteQuizResultsEvent
import com.education.ekagratagkquiz.main.util.DeleteQuizResultsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsScreenViewModel @Inject constructor(
    private val repository: QuizResultsRepository,
) : ViewModel() {

    private val idValidator = DocumentIdValidator()

    var content = mutableStateOf<ShowContent<List<QuizResultModel?>>>(ShowContent(isLoading = true))
        private set

    private val messages = MutableSharedFlow<UiEvent>()

    var deleteQuizState = mutableStateOf(DeleteQuizResultsState())
        private set

    val uiEvent = messages.asSharedFlow()


    init {
//        getResults()
    }


    fun onDeleteResult(event: DeleteQuizResultsEvent) {
        when (event) {
            is DeleteQuizResultsEvent.ResultsSelected -> {
                deleteQuizState.value = deleteQuizState.value.copy(
                    isDialogOpen = true, result = event.result
                )
            }

            DeleteQuizResultsEvent.DeleteCanceled -> {
                deleteQuizState.value =
                    deleteQuizState.value.copy(isDialogOpen = false, result = null)
            }

            DeleteQuizResultsEvent.DeleteConfirmed -> {
                deleteQuizResults()
                deleteQuizState.value =
                    deleteQuizState.value.copy(isDialogOpen = false, result = null)
            }
        }
    }

    fun getResults() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getQuizResults().onEach { res ->
                when (res) {

                    is Resource.Error -> {
                        content.value = content.value.copy(isLoading = false, content = null)
                        messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                    }

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        content.value = content.value.copy(
                            isLoading = false,
                            content = res.value?.sortedByDescending {
                                it?.finalResult
                            }
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun getResults(quizId: String) {
        Log.d("TAG", "getResults: quizId: $quizId")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getQuizResults(quizId).onEach { res ->
                when (res) {

                    is Resource.Error -> {
                        content.value = content.value.copy(isLoading = false, content = null)
                        messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                    }

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        content.value = content.value.copy(
                            isLoading = false,
                            content = res.value?.sortedByDescending {
                                it?.finalResult
                            }
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    private fun deleteQuizResults() {
        val result = deleteQuizState.value.result
        if (result != null)
            viewModelScope.launch(Dispatchers.IO) {
                when (val resp = repository.deleteQuizResults(result.uid)) {
                    is Resource.Error -> {
                        messages.emit(UiEvent.ShowSnackBar(resp.message ?: ""))
                    }

                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        messages.emit(UiEvent.ShowSnackBar("Removed Result for ${result.quiz?.subject ?: "null"}"))
                    }
                }
            }
    }


}