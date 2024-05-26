package com.education.ekagratagkquiz.contribute_quiz.domain.model

import android.net.Uri


data class CreateQuizModel(
    val subject: String,
    val desc: String,
    val color: String,
    val quizSize: Long = 0L,
    val isSection: Boolean = false,
    val path: String? = null,
    val quizList: String? = null,
    val creatorUID: String? = null,
    val image: String? = null,
    val pdf: String? = null,/*Preparation pdf*/
){
    /*override fun toString(): String {
        return Gson().toJson(this)
    }*/
}
