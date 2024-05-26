package com.education.ekagratagkquiz.main.domain.models.create_contribution_question


import com.education.ekagratagkquiz.main.domain.models.OptionType

data class CreateContributionOptionsState(
    val option: QuizOption = QuizOption(),
    val optionType: Quiz = Quiz.SingleChoice,
    val optionError: String? = null,
)

sealed class CreateContributionOptionsEvent {
    data class OptionAdded(val optionType: OptionType = OptionType.TEXT) : CreateContributionOptionsEvent(){
        fun getOption(): QuizOption{
            return when(optionType){
                OptionType.TEXT -> QuizOption(content = Content.TextContent(""))
                OptionType.IMAGE -> QuizOption(content = Content.ImageContent(""))
                else -> QuizOption(content = Content.TextContent(""))
            }
        }
    }
    data class OptionValueChanged(val option: String,val index:Int) : CreateContributionOptionsEvent()
    data class OptionRemove(val option: CreateContributionOptionsState) : CreateContributionOptionsEvent()
}
