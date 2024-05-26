package com.education.ekagratagkquiz.main.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.core.util.NavParams
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.core.util.UiEvent
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import com.education.ekagratagkquiz.main.domain.repository.FullQuizRepository
import com.education.ekagratagkquiz.main.util.FinalQuizDialogEvents
import com.education.ekagratagkquiz.main.util.FinalQuizEvent
import com.education.ekagratagkquiz.main.util.FinalQuizOptionState
import com.education.ekagratagkquiz.main.util.FinalQuizRouteState
import com.education.ekagratagkquiz.main.util.FinalQuizState
import com.education.ekagratagkquiz.main.util.FullQuizState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullQuizViewModel @Inject constructor(
    private val repo: FullQuizRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val TAG = FullQuizViewModel::class.java.simpleName


    private val _currentTime = MutableStateFlow<Long?>(null) // Nullable for initial state
    val currentTime: Flow<Long?> = _currentTime

    private val _isFinished = MutableSharedFlow<Boolean>()
    val isFinished: SharedFlow<Boolean> = _isFinished.asSharedFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: Flow<Boolean> = _isRunning.asSharedFlow()

    private val ONE_SECOND = 1000L

    private fun startTimer(timer: Long) {
        viewModelScope.launch {

            Log.d(TAG, "startTimer: timer: $timer")

            _currentTime.emit(timer)
            _isRunning.emit(true)

            Log.d(TAG, "startTimer: timer current time: ${_currentTime.value}")

            while (_isRunning.value && _currentTime.value != null && _currentTime.value!! > 0) {
                delay(ONE_SECOND)
                _currentTime.emit(_currentTime.value?.minus(ONE_SECOND))
            }

            if (_isRunning.value && _currentTime.value != null) {
                Log.d(TAG, "setResult: times up")
                onSubmit()
            }
        }
    }


    fun stopTimer() {
        viewModelScope.launch { _isRunning.emit(false) }
    }

    fun resetTimer() {
        _currentTime.value = 0L
        viewModelScope.launch { _isRunning.emit(false) }
    }


    var fullQuizState = mutableStateOf(FullQuizState())
        private set

    var routeState = mutableStateOf(FinalQuizRouteState())
        private set

    var quizState = mutableStateOf(FinalQuizState())
        private set

    var quizParcelable = mutableStateOf(QuizParcelable())
        private set

    private val messages = MutableSharedFlow<UiEvent>()
    val infoMessages = messages.asSharedFlow()

    private var quizId: String? = null

    init {
        quizId = savedStateHandle.get<String>(NavParams.QUIZ_ID)
        val valid = savedStateHandle.get<Boolean>(NavParams.SOURCE_VALID_ID)
        fullQuizState.value = fullQuizState.value.copy(isQuizPresent = valid ?: false)
        quizId?.let { id -> getQuizQuestion(id, valid) }
    }

    fun setQuiz(quiz: QuizParcelable?) {
        Log.d(TAG, "setQuiz: Quiz: $quiz")
        quizParcelable.value = quiz ?: QuizParcelable()
    }

    fun onBackClicked(message: String) {
        if (fullQuizState.value.questions.isEmpty()) {
            viewModelScope.launch { messages.emit(UiEvent.NavigateBack) }
        } else if (fullQuizState.value.isQuizResultView) {
            viewModelScope.launch { messages.emit(UiEvent.NavigateBack) }
        } else {
            viewModelScope.launch { messages.emit(UiEvent.ShowSnackBar(message)) }
        }
    }


    fun onOptionEvent(event: FinalQuizEvent) {
        when (event) {
            is FinalQuizEvent.OptionInputAnswer -> {
                Log.d(TAG, "onOptionEvent: OptionInputAnswer")

                val selectedOptions =
                    quizState.value.optionsState[event.index].option ?: mutableStateListOf()

                if (event.option.isNotEmpty()) {
                    /*If no option is selected before on a question increment attempted on new question*/
                    if (selectedOptions.isEmpty()) {
                        quizState.value = quizState.value.copy(
                            attemptedCount = quizState.value.attemptedCount + 1
                        )
                    }

                    /*If size is one then update value of index 0 otherwise add a value in 0 index*/
                    if (selectedOptions.size == 1) {
                        selectedOptions[0] =
                            QuestionOption(option = event.option, isSelected = true)
                    } else {
                        selectedOptions.add(
                            0,
                            QuestionOption(option = event.option, isSelected = true)
                        )
                    }
                }

                /*Clear attempt count when input from user is empty*/
                if (event.option.isEmpty()) {
                    quizState.value = quizState.value.copy(
                        attemptedCount = quizState.value.attemptedCount - 1
                    )


                    quizState.value.optionsState[event.index] =
                        quizState.value.optionsState[event.index].copy(
                            option = mutableStateListOf(),
                            isCorrect = false
                        )
                    selectedOptions.clear()
                }


                val selectedOption = selectedOptions.firstOrNull()?.option ?: ""
                val correctOption =
                    fullQuizState.value.questions[event.index]?.options?.firstOrNull()?.option ?: ""

                val isOptionCorrect =
                    ("INPUT:$selectedOption").lowercase() == correctOption.lowercase()

                /*Update result when selectOption and correction option are same.*/
                quizState.value.optionsState[event.index] =
                    quizState.value.optionsState[event.index].copy(
                        option = selectedOptions,
                        isCorrect = isOptionCorrect
                    )
            }

            is FinalQuizEvent.OptionPicked -> {
                Log.d(TAG, "onOptionEvent: OptionPicked")
                Log.d(
                    TAG, "onOptionEvent: \n" +
                            "\nindex    ${event.index}" +
                            "\nquestion ${event.question}" +
                            "\nopt sel  ${quizState.value.optionsState[event.index].option?.map { it.option + " ${it.isSelected} || " }}" +
                            "\noption   ${event.option}" +
                            "\nopt stat ${quizState.value.optionsState.size}" +
                            "\nopt stat ${quizState.value.optionsState.map { it.option?.size }}" +
                            "\nopt stat ${quizState.value.optionsState.map { it.option?.map { it.option } }}" +
                            ""
                )

                val selectedOptions =
                    quizState.value.optionsState[event.index].option ?: mutableStateListOf()
                val allOptions = event.question.options

                /*If no option is selected before on a question increment attempted on new question*/
                if (selectedOptions.isEmpty()) {
                    quizState.value = quizState.value.copy(
                        attemptedCount = quizState.value.attemptedCount + 1
                    )
                }

                /*If selected option already picked again remove that for multiple choice question*/
                // TODO: If it is single choice always remove previous selected option
                if (selectedOptions.contains(event.option)) {
                    selectedOptions.remove(event.option)
                    if (selectedOptions.isEmpty()) {
                        quizState.value = quizState.value.copy(
                            attemptedCount = quizState.value.attemptedCount - 1
                        )
                    }
                } else {
                    selectedOptions.add(event.option)
                }

                val hasAllSelectedIsCorrect =
                    hasAllSelectedOptionsIsTrue(selectedOptions, allOptions)
                /*show that selected option in ui and update question is selected correct option or not*/
                quizState.value.optionsState[event.index] =
                    quizState.value.optionsState[event.index].copy(
                        option = selectedOptions,
                        isCorrect = hasAllSelectedIsCorrect
                    )


                val count = quizState.value.optionsState.count { it.isCorrect }
                Log.d(
                    TAG, "onOptionEvent: \n" +
                            "\nindex           ${event.index}" +
                            "\nallOptions      ${allOptions.map { it.option + " ${it.isSelected}" }}" +
                            "\nselectedOptions ${selectedOptions.map { it.option + " ${it.isSelected}" }}" +
                            "\nhasAllSelectedIsCorrect -> $hasAllSelectedIsCorrect" +
                            "\nTotal Correct Answer -> ${count}" +
                            ""
                )
            }

            is FinalQuizEvent.OptionUnpicked -> {
                if (quizState.value.optionsState[event.index].option?.isNotEmpty() == true) {
                    quizState.value = quizState.value.copy(
                        attemptedCount = quizState.value.attemptedCount - 1
                    )
                }

                quizState.value.optionsState[event.index] =
                    quizState.value.optionsState[event.index].copy(
                        option = mutableStateListOf(),
                        isCorrect = false
                    )
            }

            FinalQuizEvent.SubmitQuiz -> {
                routeState.value = routeState.value.copy(
                    showDialog = true,
                    isBackNotAllowed = false
                )
            }
        }
    }

    private fun hasAllSelectedOptionsIsTrue(
        selectedOptions: List<QuestionOption>,
        allOptions: List<QuestionOption>
    ): Boolean {
        // Checking is there any option selected is false then quiz is not correct.
        val containsFalse = selectedOptions.any { !it.isSelected }
        // Checking that size of selected is equal to allOptions where option contains true
        return selectedOptions.size == allOptions.count { it.isSelected } && !containsFalse
    }

    fun onDialogEvent(event: FinalQuizDialogEvents) {
        when (event) {
            FinalQuizDialogEvents.ContinueQuiz -> {
                routeState.value = routeState.value.copy(
                    showDialog = false,
                    isBackNotAllowed = false
                )
            }

            FinalQuizDialogEvents.SubmitQuiz -> {
                routeState.value = routeState.value.copy(
                    showDialog = false,
                    isBackNotAllowed = true
                )

                Log.d(TAG, "setResult: onSubmit clicked")
                onSubmit()
            }
        }
    }


    private fun onSubmit() {
        val count = quizState.value.optionsState.count { it.isCorrect }
        /*Selected Option*/
        quizState.value.optionsState.forEach {
            Log.d(
                TAG,
                "onSubmit: isCorrect = ${it.isCorrect}, option = ${it.option?.map { it.option }}"
            )
        }
        Log.d(TAG, "onSubmit: count = $count")

        /*All option*/
        fullQuizState.value.questions.forEach {
            Log.d(TAG, "onSubmit: quiz   ${it.toString()}")
        }

        viewModelScope.launch {

            _isFinished.emit(true)
            _isRunning.emit(false)


            if (quizState.value.optionsState.isEmpty()) {
                messages.emit(UiEvent.ShowSnackBar("Cannot add result for this quiz as there are no questions found"))
                return@launch
            }
            val result = QuizResultModel(
                quizId = quizId!!,
                totalQuestions = quizState.value.optionsState.size,
                correct = count,
                attempt = quizState.value.attemptedCount,
                quiz = fullQuizState.value.quiz,
                quizTitle = quizParcelable.value.subject
            )

            Log.d(TAG, "onSubmit: quiz title: ${quizParcelable.value.subject ?: ""}")

            fullQuizState.value = fullQuizState.value.copy(
                isResultViewLoading = true,
                quizResult = result
            )


            Log.d(TAG, "setResult: viewmodel")
            repo.setResult(result).onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        messages.emit(UiEvent.ShowSnackBar(res.message ?: ""))
                        fullQuizState.value = fullQuizState.value.copy(
                            isResultViewLoading = false,
                            isQuizResultView = true,
                            quizResult = result
                        )
                    }

                    is Resource.Loading -> {
                        messages.emit(UiEvent.ShowSnackBar("Submitting your quiz"))
                    }

                    is Resource.Success -> {
                        fullQuizState.value = fullQuizState.value.copy(
                            isResultViewLoading = false,
                            isAdView = true,
                            quizResult = result
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun showResultView() {
        fullQuizState.value = fullQuizState.value.copy(isQuizResultView = true, isAdView = false)
    }


    private fun getQuizQuestion(uid: String, valid: Boolean?) {
        viewModelScope.launch {
            if (valid == false)
                when (val quiz = repo.getCurrentQuiz(uid)) {
                    is Resource.Error -> {
                        fullQuizState.value = fullQuizState.value.copy(
                            isLoading = false, quiz = null
                        )
                        messages.emit(UiEvent.ShowSnackBar(message = quiz.message ?: ""))
                        return@launch
                    }

                    is Resource.Success -> {
                        fullQuizState.value = fullQuizState.value.copy(
                            isLoading = false, quiz = quiz.value
                        )
                        startTimer(fullQuizState.value.questions.size * ONE_SECOND)
                    }

                    else -> {}
                }
            fullQuizState.value = fullQuizState.value.copy(
                isLoading = false, quiz = null
            )
            repo.getAllQuestions(uid).onEach { res ->
                when (res) {
                    is Resource.Error -> {
                        fullQuizState.value = fullQuizState.value
                            .copy(
                                isQuestionLoading = false,
                                questions = emptyList()
                            )
                        messages.emit(UiEvent.ShowSnackBar(message = res.message ?: ""))
                    }

                    is Resource.Success -> {
                        quizState.value.optionsState.addAll(
                            List(size = res.value?.size ?: 0) { FinalQuizOptionState() }
                        )
                        fullQuizState.value = fullQuizState.value
                            .copy(
                                isQuestionLoading = false,
                                questions = (res.value!!).shuffled()
                            )

                        startTimer(fullQuizState.value.questions.size * ONE_SECOND * 60)
                    }

                    is Resource.Loading -> {
                        fullQuizState.value = fullQuizState.value
                            .copy(isQuestionLoading = true)
                    }
                }
            }.launchIn(this)
        }
    }


}