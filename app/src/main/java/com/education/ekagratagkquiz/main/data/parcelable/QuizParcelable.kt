package com.education.ekagratagkquiz.main.data.parcelable

import android.os.Parcelable
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizParcelable(
    val uid: String = "",
    val subject: String = "",
    val quizSize: Long = 0L,
    val isSection: Boolean = false,
    val path: String? = null,
    val pdf: String? = null,
    val desc: String? = null,
) : Parcelable

fun QuizModel.toParcelable(): QuizParcelable {
    return QuizParcelable(
        uid = uid,
        isSection = isSection,
        quizSize = quizSize,
        path = path,
        pdf = pdf,
        desc = desc,
        subject = subject
    )
}
