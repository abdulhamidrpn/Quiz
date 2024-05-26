package com.education.ekagratagkquiz.contribute_quiz.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.data.mappers.toModel
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuizRepository
import com.education.ekagratagkquiz.contribute_quiz.domain.use_cases.CreateQuizValidator
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuizState
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.PreparationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: CreateQuizRepository,
    private val user: FirebaseUser?,
) : ViewModel() {

    val TAG = QuizViewModel::class.java.simpleName
    var quizId by mutableStateOf("")
    var sectionId by mutableStateOf("")

    private val messages = MutableSharedFlow<UiEvent>()

    val messagesFlow = messages.asSharedFlow()

    private val quizValidator = CreateQuizValidator()

    var isSection =
        mutableStateOf(CreateQuizState(isSection = false))
        private set

    var createQuiz =
        mutableStateOf(CreateQuizState(createdBy = user?.displayName, creatorUID = user?.uid))
        private set

    var quizDialogState = mutableStateOf(false)
        private set


    fun onCreateQuizEvent(events: CreateQuizEvents) {
        when (events) {
            is CreateQuizEvents.ObDescChange -> {
                createQuiz.value = createQuiz.value.copy(
                    desc = events.desc, descError = null
                )
            }

            is CreateQuizEvents.OnUpdatedToSection -> {
                createQuiz.value = createQuiz.value.copy(
                    isSection = events.isSection
                )
            }

            is CreateQuizEvents.OnPdfAdded -> {
                createQuiz.value = createQuiz.value.copy(pdf = events.uri)
            }

            is CreateQuizEvents.OnImageAdded -> {
                createQuiz.value = createQuiz.value.copy(image = events.uri)
            }

            is CreateQuizEvents.OnSubjectChanges -> {
                createQuiz.value = createQuiz.value.copy(
                    subject = events.subject, subjectError = null
                )
            }

            CreateQuizEvents.OnImageRemoved -> {
                createQuiz.value = createQuiz.value.copy(
                    image = null
                )
            }

            CreateQuizEvents.OnPdfRemoved -> {
                createQuiz.value = createQuiz.value.copy(
                    pdf = null
                )
            }

            is CreateQuizEvents.OnColorAdded -> {
                createQuiz.value = createQuiz.value.copy(
                    color = events.color
                )
            }

            is CreateQuizEvents.OnSubmit -> {
                validate(events.quiz)
            }
        }
    }

    private fun validate(quiz: QuizParcelable?) {
        // TODO: Once an quiz is uploaded from a section, Under that section can't create new section there.
        Log.d(TAG, "createQuiz: validate: ${quiz.toString()} ")
        val validateSubject = quizValidator.validateSubject(createQuiz.value)
        val validateDesc = quizValidator.validateDesc(createQuiz.value)
        val errors = listOf(validateSubject, validateDesc).any { !it.isValid }

        if (errors) {
            createQuiz.value = createQuiz.value.copy(
                subjectError = validateSubject.message,
                descError = validateDesc.message
            )

            return
        } else {
            createQuiz.value = createQuiz.value.copy(
                path = quiz?.path,
                subjectError = null,
                descError = null
            )
            addFireBaseQuiz()
        }
    }

    private fun addFireBaseQuiz() {
        viewModelScope.launch(Dispatchers.IO) {
            messages.emit(UiEvent.ShowSnackBar("Adding this quiz please wait"))
            createQuiz.value.image?.let { image ->
                val uri = repository.uploadQuizImage(image.toString())
                createQuiz.value = createQuiz.value.copy(
                    image = Uri.parse(uri)
                )
            }
            createQuiz.value.pdf?.let {pdf ->
                val uri = repository.uploadQuizPdf(pdf.toString())
                createQuiz.value = createQuiz.value.copy(
                    pdf = Uri.parse(uri)
                )
            }

            repository.createQuiz(createQuiz.value.toModel())
                .onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            quizDialogState.value = false
                            messages.emit(UiEvent.ShowSnackBar(resource.message ?: ""))
                        }

                        is Resource.Success -> {
                            quizDialogState.value = true
                            createQuiz.value = CreateQuizState(
                                createdBy = user?.displayName,
                                creatorUID = user?.uid
                            )
                        }

                        is Resource.Loading -> messages.emit(UiEvent.ShowSnackBar("Adding this quiz please wait"))
                    }
                }.launchIn(this)
        }
    }


    fun updateSectionId(id: String? = null) {
        Log.d(TAG, "updateSectionId: id = $id")
        if (id != null) {
            sectionId = id
        }
    }

    fun updateQuizId(id: String?) {
        Log.d(TAG, "updateQuizId: id = $id")
        if (id != null) {
            quizId = id
        }
    }
}