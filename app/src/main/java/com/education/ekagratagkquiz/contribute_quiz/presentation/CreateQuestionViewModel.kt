package com.education.ekagratagkquiz.contribute_quiz.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.data.mappers.toModel
import com.education.ekagratagkquiz.contribute_quiz.data.mappers.toState
import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuestionsModel
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuestionsRepo
import com.education.ekagratagkquiz.contribute_quiz.uses_cases.CreateQuestionValidator
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionEvents
import com.education.ekagratagkquiz.contribute_quiz.util.ExcelQuestionsState
import com.education.ekagratagkquiz.contribute_quiz.util.OptionsEvent
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionsViewMode
import com.education.ekagratagkquiz.core.util.CreateQuestionEvent
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.domain.models.OptionType
import com.education.ekagratagkquiz.main.domain.models.QuestionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateQuestionViewModel @Inject constructor(
    private val repo: CreateQuestionsRepo, val user: FirebaseUser?
) : ViewModel() {
    val TAG = "CreateQuestionViewModel"
    private val validator = CreateQuestionValidator()


    var excelQuestionsState = mutableStateOf(ExcelQuestionsState())
        private set

    var questions = mutableStateListOf(CreateQuestionState())
        private set

    private val messages = MutableSharedFlow<UiEvent>()

    val uiEvents = messages.asSharedFlow()

    var showDialog = mutableStateOf(false)
        private set

    fun onExcelQuestionEvents(event: ExcelQuestionEvents) {
        when (event) {
            is ExcelQuestionEvents.readFile -> {
                excelQuestionsState.value = excelQuestionsState.value.copy(
                    isRequestPermission = false,
                    fileUri = event.uri?.toString()
                )

                if (event.uri != null && !event.quizId.isNullOrEmpty()) {
                    readExcelData(event.uri, event.quizId)
                }
            }

            ExcelQuestionEvents.OnRequestPermission -> {
                excelQuestionsState.value = excelQuestionsState.value.copy(
                    isRequestPermission = true
                )
            }

            else -> {

            }
        }
    }

    private fun readExcelData(uri: Uri, quizId: String) {
        viewModelScope.launch {
            try {
                repo.readExcelData(uri, quizId).onEach { res ->
                    when (res) {
                        is Resource.Error -> {
                            showDialog.value = false
                            messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                        }

                        is Resource.Success -> {
                            res.value?.forEach {
                                val excelCreateQuestionsState = it?.toState() ?: CreateQuestionState()
                                Log.d(TAG, "readExcelData: $it")
                                questions.add(excelCreateQuestionsState)
                            }
                        }

                        is Resource.Loading -> showDialog.value = true
                    }
                }.launchIn(this)

            } catch (e: Exception) {
                Log.e("MyViewModel", "Error reading Excel file:", e)
            }
        }
    }

    private fun onOptionsEvent(event: OptionsEvent, index: Int) {
        val currentQuestion = questions[index]
        when (event) {
            is OptionsEvent.OptionAdded -> {
                Log.d(TAG, "onOptionsEvent: event: ${event.optionType}")
                when (event.optionType) {
                    OptionType.INPUT -> {
                        /*Remove all option when add a new input type option.*/
                        currentQuestion.options.clear()
                        currentQuestion.options.add(QuestionOptionsState(optionType = event.optionType))
                    }

                    else -> {
                        /*Remove input field if previously added there any input option*/
                        if (currentQuestion.options.any { it.optionType == OptionType.INPUT }) {
                            currentQuestion.options.clear()
                        }
                        currentQuestion.options.add(QuestionOptionsState(optionType = event.optionType))
                    }
                }
            }

            is OptionsEvent.OptionRemove -> {
                if (currentQuestion.options.size != 1) {
                    currentQuestion.options.remove(event.option)
                    return
                }
                viewModelScope.launch {
                    messages.emit(
                        UiEvent.ShowSnackBar(
                            message = "Cannot create a question without any options"
                        )
                    )
                }
            }

            is OptionsEvent.OptionValueChanged -> {
                currentQuestion.options[event.index] = currentQuestion.options[event.index].copy(
                    option = event.value, optionError = null
                )
            }
        }
    }

    fun onQuestionEvent(event: CreateQuestionEvent) {
        when (event) {
            is CreateQuestionEvent.DescriptionAdded -> {
                val index = questions.indexOf(event.question)
                questions[index] = questions[index].copy(desc = event.desc)
            }

            is CreateQuestionEvent.ExplanationAdded -> {
                val index = questions.indexOf(event.question)
                questions[index] = questions[index].copy(questionExplanation = event.explanation)
            }

            is CreateQuestionEvent.ToggleQuestionType -> {
                val index: Int = questions.indexOf(event.question)
                questions[index] = questions[index].copy(
                    questionType =
                    if (questions[index].questionType == QuestionType.TEXT) QuestionType.IMAGE
                    else QuestionType.TEXT
                )
            }

            is CreateQuestionEvent.OnOptionEvent -> {
                Log.d(TAG, "onQuestionEvent: ${event.optionEvent}")
                val index = questions.indexOf(event.question)
                onOptionsEvent(event.optionEvent, index)
            }

            CreateQuestionEvent.QuestionAdded -> {
                questions.add(CreateQuestionState())
            }

            is CreateQuestionEvent.QuestionQuestionAdded -> {
                val index = questions.indexOf(event.question)
                if (index != -1)
                    questions[index] =
                        questions[index].copy(
                            question = event.value,
                            questionError = null,
                            isDeleteAllowed = event.value.isEmpty()
                        )
            }

            is CreateQuestionEvent.QuestionRemoved -> {
                if (questions.size != 1) {
                    questions.remove(event.question)
                    return
                }
                viewModelScope.launch {
                    messages.emit(
                        UiEvent.ShowSnackBar(
                            message = "There should be at least one question to be added"
                        )
                    )
                    cancel()
                }
            }

            is CreateQuestionEvent.ToggleQuestionExplanation -> {
                val index = questions.indexOf(event.question)
                questions[index] = questions[index].copy(
                    questionExplanation = if (questions[index].questionExplanation == null) "" else null
                )
            }

            is CreateQuestionEvent.ToggleQuestionDesc -> {
                val index = questions.indexOf(event.question)
                questions[index] = questions[index].copy(
                    desc = if (questions[index].desc == null) "" else null
                )
            }

            is CreateQuestionEvent.ToggleRequiredField -> {
                val index = questions.indexOf(event.question)
                val isRequired = questions[index].required
                questions[index] = questions[index].copy(required = !isRequired)
            }

            is CreateQuestionEvent.SetEditableMode -> {
                val index = questions.indexOf(event.question)
                questions[index] = questions[index].copy(state = QuestionsViewMode.Editable)
            }

            is CreateQuestionEvent.SetNotEditableMode -> {
                val index = questions.indexOf(event.question)
                val isValid = runValidation(questions[index])
                if (isValid)
                    questions[index] = questions[index].copy(state = QuestionsViewMode.NonEditable)
            }

            is CreateQuestionEvent.SetCorrectOption -> {


                val index = questions.indexOf(event.question)

                questions[index].options[event.optionIndex] =
                    questions[index].options[event.optionIndex].copy(
                        option = event.option.option,
                        isSelected = !event.option.isSelected,
                        optionError = null
                    )


            }

            is CreateQuestionEvent.SubmitQuestions -> {
                /*if (ansKeyValidation()) {
                    viewModelScope.launch {
                        messages.emit(UiEvent.ShowSnackBar("Some ans-keys are not set, set them !"))
                    }
                    return
                }*/
                try {
                    if (allQuestionValid()) onSubmit(event.quidId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun runValidation(question: CreateQuestionState): Boolean {

        val questionValidation = validator.validateQuestion(question)
        val index = questions.indexOf(question)

        // this local variable helps to solve the problem with
        // java.util.ConcurrentModificationException
        // changing the option at one go will trigger the loop rendering all the options again
        // Thus creating the local variable and changing them at one go
        val optionErrors: MutableList<QuestionOptionsState> = mutableStateListOf()

        val optionsValidation = question.options.map { option ->
            val vOption = validator.validateOptions(option)
            optionErrors.add(
                QuestionOptionsState(
                    option = option.option,
                    isSelected = option.isSelected,
                    optionType = option.optionType,
                    optionError = if (!vOption.isValid) vOption.message else null
                )
            )
            vOption
        }
        questions[index] =
            question.copy(options = optionErrors, questionError = questionValidation.message)
        return questionValidation.isValid && optionsValidation.all { it.isValid }
    }

    private fun allQuestionValid() =
        questions.map { question -> runValidation(question) }.all { it }


    private fun onSubmit(id: String) {
        Log.d("TAG", "onSubmit: $id")
        val models = questions.map { it.toModel().copy(quizId = id) }
        viewModelScope.launch {

            showDialog.value = true

            val updatedObjects = mutableListOf<CreateQuestionsModel>()
            models.forEach { questionModel ->

                var updatedQuestion = questionModel.question
                val updatedOptions = mutableStateListOf<QuestionOption>()
                var updatedAnswer = questionModel.correctAnswer


                if (!questionModel.isSourceExcel) {
                    /*Uploading Option Image*/
                    questionModel.options.forEach { option ->

                        if (option.option.contains("IMAGE:")) {
                            val uploadedOption = option.option.split("IMAGE:").lastOrNull()
                                ?.let { it1 -> repo.uploadImage(it1) }

                            Log.d(TAG, "onSubmit: uploadedOption: $uploadedOption")
                            updatedOptions.add(
                                option.copy(
                                    option = "IMAGE:" + Uri.parse(
                                        uploadedOption
                                    )
                                )
                            ) // Update object with URL
                        } else {
                            updatedOptions.add(option)
                        }
                    }


                    /*Uploading Image Question*/
                    if (questionModel.question.contains("IMAGE:")) {
                        val questionImage = questionModel.question.split("IMAGE:").lastOrNull()
                            ?.let { it1 -> repo.uploadImage(it1) } ?: questionModel.question
                        updatedQuestion = "IMAGE:" + Uri.parse(questionImage)
                        Log.d(TAG, "onSubmit: uploadedQuestion: $updatedQuestion")
                    }

                }


                updatedObjects.add(
                    questionModel.copy(
                        question = updatedQuestion,
                        options = updatedOptions,
                        correctAnswer = updatedAnswer
                    )
                )
            }
            Log.d(TAG, "onSubmit: models ${updatedObjects.toString()}")



            repo.createQuestionsToQuiz(updatedObjects).onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        showDialog.value = false
                        messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                    }

                    is Resource.Success -> messages.emit(UiEvent.NavigateBack)
                    is Resource.Loading -> showDialog.value = true
                }
            }.launchIn(this)
        }
    }
}