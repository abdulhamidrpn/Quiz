package com.education.ekagratagkquiz.main.data.firebase_dto


import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.education.ekagratagkquiz.main.domain.models.PreparationModel
import com.education.ekagratagkquiz.main.domain.models.QuestionModel

@IgnoreExtraProperties
data class PreparationDto(
    @DocumentId val id: String = "",
    val subject: String = "",
    val desc: String? = null,
    val path: String? = null,
    @ServerTimestamp val timestamp: Timestamp? = null
){

    fun toModel(): PreparationModel {
        return PreparationModel(
            id = id,
            subject = subject,
            desc = desc,
            path = path,
            timestamp = timestamp
        )
    }
}
