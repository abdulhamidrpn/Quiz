package com.education.ekagratagkquiz.main.data.firebase_dto


import android.os.Build
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

@IgnoreExtraProperties
data class QuizDto(
    @DocumentId val id: String = "",
    val subject: String = "",
    val desc: String? = null,
    val image: String? = null,
    val quizSize: Long = 0L,
    @PropertyName("section") val isSection: Boolean = false,
    val path: String? = null,
    val pdf: String? = null,
    @ServerTimestamp val timestamp: Timestamp? = null,
    val color: String? = null,
    val creatorUID: String? = null,
    @PropertyName("approved") val isApproved: Boolean = false
) {
    fun toModel(): QuizModel {
        return QuizModel(uid = id,
            subject = subject,
            desc = desc,
            image = image,
            quizSize = quizSize,
            isSection = isSection,
            path = path,
            pdf = pdf,
            creatorUID = creatorUID,
            lastUpdate = getLastUpdated(),
            color = color, isApproved = isApproved
        )
    }

    private fun getLastUpdated(): LocalDateTime? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timestamp?.let {
                LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(it.seconds), TimeZone.getDefault().toZoneId()
                )
            } ?: LocalDateTime.now()
        } else {
            null
        }
    }

}
