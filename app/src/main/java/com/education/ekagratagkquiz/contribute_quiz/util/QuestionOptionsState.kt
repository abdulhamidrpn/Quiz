package com.education.ekagratagkquiz.contribute_quiz.util

import com.education.ekagratagkquiz.main.domain.models.OptionType


data class QuestionOptionsState(
    val option: String = "",
    val isSelected: Boolean = false,
    val optionType: OptionType = OptionType.TEXT,
    val optionError: String? = null,
)

sealed class OptionsEvent {
    data class OptionAdded(val optionType: OptionType = OptionType.TEXT) : OptionsEvent()
    data class OptionValueChanged(val value: String, val index: Int) : OptionsEvent()
    data class OptionRemove(val option: QuestionOptionsState) : OptionsEvent()
}
