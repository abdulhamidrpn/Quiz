package com.education.ekagratagkquiz.contribute_quiz.data.mappers

import com.education.ekagratagkquiz.contribute_quiz.data.dto.CreateQuizDto
import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuizModel
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuizState

fun CreateQuizState.toModel(): CreateQuizModel {
    // These are the base colors that will be added if no color is chosen
    val colors = listOf("#fca5a5", "#86efac", "#93c5fd", "#fde047").shuffled()
    return CreateQuizModel(
        subject = subject,
        desc = desc,
        isSection = isSection,
        path = path,
        pdf = pdf?.toString(),
        color = color?.let {
            "#" + it.toString(16).removeSuffix("00000000")
        } ?: colors[0],
        image = image?.toString(),
        creatorUID = creatorUID ?: "",
    )
}

fun CreateQuizModel.toDto(): CreateQuizDto {
    return CreateQuizDto(
        subject = subject,
        desc = desc,
        isSection = isSection,
        quizSize = quizSize,
        path = path,
        pdf = pdf,
        color = color,
        image = image,
        creatorUID = creatorUID,
    )
}