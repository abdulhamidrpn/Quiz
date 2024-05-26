package com.education.ekagratagkquiz.contribute_quiz.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class CreateQuizDto(
    val subject: String = "",
    val desc: String = "",
    val quizSize: Long = 0L,
    val isSection: Boolean = false,
    val color: String? = null,
    val path: String? = null,
    val image: String? = null,
    val pdf: String? = null,
    val creatorUID: String? = null,
    @ServerTimestamp val timestamp: Timestamp? = null,
    @PropertyName("approved") val isApproved: Boolean = false
)