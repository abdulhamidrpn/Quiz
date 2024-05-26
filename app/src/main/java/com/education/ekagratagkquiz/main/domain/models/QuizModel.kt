package com.education.ekagratagkquiz.main.domain.models

import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import java.time.LocalDateTime

data class QuizModel(
    val uid: String,
    val subject: String,
    val desc: String? = null,
    val quizSize: Long = 0L,
    val image: String? = null,
    val creatorUID: String? = null,
    val isSection: Boolean = false,
    val path: String? = null,
    val pdf: String? = null,
    val color: String? = null,
    val lastUpdate: LocalDateTime? = null,
    val isApproved: Boolean
)

fun QuizParcelable.toModel(): QuizModel {
    return QuizModel(
        uid = uid,
        isSection = isSection,
        quizSize = quizSize,
        path = path,
        pdf = pdf,
        desc = desc,
        subject = subject,
        isApproved = true
    )
}