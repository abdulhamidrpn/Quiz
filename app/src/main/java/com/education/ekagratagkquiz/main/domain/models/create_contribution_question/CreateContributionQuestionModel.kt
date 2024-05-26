package com.education.ekagratagkquiz.main.domain.models.create_contribution_question


data class CreateContributionQuestionModel(
    val description: String? = null,
    val isRequired: Boolean = false,
    val question: Content,
    val type: Quiz,
    val options: List<QuizOption> = emptyList(),
    val answer: String? = null, // Only for UserInput
    val quizId: String? = null
)

sealed class Content(val type: String) {
    data class TextContent(val text: String) : Content(type = "Text")
    data class ImageContent(val url: String, val uploaded: Boolean = false) :
        Content(type = "Image")
}

fun Content.getData(): String {
    return when (this) {
        is Content.ImageContent -> this.url
        is Content.TextContent -> this.text
    }
}
fun Content.setData(value:String): Content {
    return when (this) {
        is Content.ImageContent -> Content.ImageContent(value)
        is Content.TextContent -> Content.TextContent(value)
    }
}

sealed class Quiz(val questionType: String) {
    data object SingleChoice : Quiz("SingleChoice")
    data object MultipleChoice : Quiz("MultipleChoice")
    data object UserInput : Quiz("UserInput")
}

data class QuizOption(
    val content: Content = Content.TextContent(""),
    val isSelected: Boolean = false
)
