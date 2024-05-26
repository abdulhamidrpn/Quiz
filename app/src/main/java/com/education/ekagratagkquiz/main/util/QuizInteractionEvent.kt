package com.education.ekagratagkquiz.main.util

import com.education.ekagratagkquiz.main.domain.models.QuizModel

sealed class QuizInteractionEvents {
    data class QuizSelected(val quiz: QuizModel) : QuizInteractionEvents()
    object QuizUnselect : QuizInteractionEvents()
}