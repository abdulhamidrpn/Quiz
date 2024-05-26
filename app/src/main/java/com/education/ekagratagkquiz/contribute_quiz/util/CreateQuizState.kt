package com.education.ekagratagkquiz.contribute_quiz.util

import android.net.Uri

data class CreateQuizState(
    val subject: String = "",
    val subjectError: String? = null,
    val desc: String = "",
    val isSection: Boolean = false,
    val path: String? = null,
    val color: ULong? = null,
    val descError: String? = null,
    val image: Uri? = null,
    val pdf: Uri? = null,/*Preparation pdf*/
    val createdBy: String? = null,
    val creatorUID: String? = null
)
