package com.education.ekagratagkquiz.main.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuestionsRepo
import com.education.ekagratagkquiz.contribute_quiz.util.OptionsEvent
import com.education.ekagratagkquiz.contribute_quiz.util.QuestionOptionsState
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.Content
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.CreateContributionOptionsEvent
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.CreateContributionOptionsState
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.CreateContributionQuestionState
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.getData
import com.education.ekagratagkquiz.main.domain.models.create_contribution_question.setData
import com.education.ekagratagkquiz.main.util.contribution.CreateContributionQuestionEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateContributionQuestionViewModel @Inject constructor(
    private val repo: CreateQuestionsRepo, val user: FirebaseUser?
) : ViewModel() {

    val TAG = "CreateContributionTAG"

    private val messages = MutableSharedFlow<UiEvent>()

    val uiEvents = messages.asSharedFlow()

    var showDialog = mutableStateOf(false)
        private set

    var questions = mutableStateListOf(CreateContributionQuestionState())
        private set


    fun onQuestionEvent(event: CreateContributionQuestionEvent) {
        when (event) {
            is CreateContributionQuestionEvent.DescriptionAdded -> {

            }

            is CreateContributionQuestionEvent.QuestionQuestionAdded -> {


                val index = questions.indexOf(event.question)
                if (index != -1)
                    questions[index] =
                        questions[index].copy(
                            question = event.question.question.setData(event.value),
                            questionError = null,
                            isDeleteAllowed = event.value.isEmpty()
                        )
            }

            is CreateContributionQuestionEvent.ToggleQuestionType -> {
                val index: Int = questions.indexOf(event.question)
                questions[index] = questions[index].copy(
                    question = when (event.question.question) {
                        is Content.TextContent -> Content.ImageContent(questions[index].question.getData())
                        is Content.ImageContent -> Content.TextContent(questions[index].question.getData())
                    }
                )
            }

            CreateContributionQuestionEvent.QuestionAdded -> {

            }

            is CreateContributionQuestionEvent.OnOptionEvent -> {

                Log.d(TAG, "onQuestionEvent: ${event.optionEvent}")
                val index = questions.indexOf(event.question)
                onOptionsEvent(event.optionEvent, index)
            }



            is CreateContributionQuestionEvent.QuestionRemoved -> {

            }

            is CreateContributionQuestionEvent.ToggleQuestionDesc -> {

            }

            is CreateContributionQuestionEvent.ToggleRequiredField -> {

            }

            is CreateContributionQuestionEvent.SetEditableMode -> {

            }

            is CreateContributionQuestionEvent.SetNotEditableMode -> {

            }

            is CreateContributionQuestionEvent.SetCorrectOption -> {

            }

            is CreateContributionQuestionEvent.SubmitQuestions -> {

            }
        }
    }


    private fun onOptionsEvent(event: CreateContributionOptionsEvent, index: Int) {
        val currentQuestion = questions[index]
        when (event) {
            is CreateContributionOptionsEvent.OptionAdded -> {
                Log.d(TAG, "onOptionsEvent: event: ${event.getOption()}")
                currentQuestion.options.add(CreateContributionOptionsState(option = event.getOption()))
            }

            is CreateContributionOptionsEvent.OptionRemove -> {
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

            is CreateContributionOptionsEvent.OptionValueChanged -> {
                /*currentQuestion.options[event.index] = currentQuestion.options[event.index].copy(
                    option = event.option, optionError = null
                )*/
            }
        }
    }

    /*val TAG = "CreateQuestionViewModel"
    private val validator = CreateQuestionValidator()

    var questions = mutableStateListOf(CreateContributionQuestionState())
        private set


    var showDialog = mutableStateOf(false)
        private set

    private fun onOptionsEvent(event: OptionsEvent, index: Int) {
        val currentQuestion = questions[index]
        when (event) {
            is OptionsEvent.OptionAdded -> {
                Log.d(TAG, "onOptionsEvent: event: ${event.optionType}")
                currentQuestion.options.add(QuestionOptionsState(optionType = event.optionType))
            }

            is OptionsEvent.OptionRemove -> {
                if (currentQuestion.options.size != 1) {
                    currentQuestion.options.remove(event.option)
                    val isRemovedQuestionIsAns = currentQuestion.ansKey == event.option
                    if (isRemovedQuestionIsAns)
                        questions[index] = currentQuestion.copy(ansKey = null)
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
                questions.add(CreateContributionQuestionState())
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
                questions[index] = questions[index].copy(ansKey = event.option)
            }

            is CreateQuestionEvent.SubmitQuestions -> {
                if (ansKeyValidation()) {
                    viewModelScope.launch {
                        messages.emit(UiEvent.ShowSnackBar("Some ans-keys are not set, set them !"))
                    }
                    return
                }
                try {
                    if (allQuestionValid()) onSubmit(event.quidId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun runValidation(question: CreateContributionQuestionState): Boolean {

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

    private fun ansKeyValidation() = questions.any { it.ansKey == null }

    private fun onSubmit(id: String) {
        Log.d("TAG", "onSubmit: $id")
        val models = questions.map { it.toModel().copy(quizId = id) }
        viewModelScope.launch {

            showDialog.value = true

            val updatedObjects = mutableListOf<CreateQuestionsModel>()
            models.forEach { questionModel ->

                var updatedQuestion = questionModel.question
                val updatedOptions = mutableStateListOf<String>()
                var updatedAnswer = questionModel.correctAnswer


                *//*Uploading Option Image*//*
                questionModel.options.forEach { option ->

                    if (option.contains("IMAGE:")) {
                        val uploadedOption = option.split("IMAGE:").lastOrNull()
                            ?.let { it1 -> repo.uploadImage(it1) }
                        if (questionModel.correctAnswer.equals(option)) {
                            updatedAnswer = "IMAGE:" + Uri.parse(uploadedOption)
                        }
                        Log.d(TAG, "onSubmit: uploadedOption: $uploadedOption")
                        updatedOptions.add("IMAGE:" + Uri.parse(uploadedOption)) // Update object with URL
                    } else {
                        updatedOptions.add(option)
                    }
                }


                *//*Uploading Image Question*//*
                if (questionModel.question.contains("IMAGE:")) {
                    val questionImage = questionModel.question.split("IMAGE:").lastOrNull()
                        ?.let { it1 -> repo.uploadImage(it1) } ?: questionModel.question
                    updatedQuestion = "IMAGE:" + Uri.parse(questionImage)
                    Log.d(TAG, "onSubmit: uploadedQuestion: $updatedQuestion")
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
    }*/
}