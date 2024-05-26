package com.education.ekagratagkquiz.main.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.main.data.firebase_dto.CreateQuizResultsDto
import com.education.ekagratagkquiz.main.data.firebase_dto.PreparationDto

data class PreparationModel(
    val id: String = "",
    val subject: String = "",
    val desc: String? = null,
    val path: String? = null,
    val timestamp: Timestamp? = null
){

    fun toDto(): PreparationDto {
        return PreparationDto(
            path = path,
            subject = subject,
            desc = desc
        )
    }
}