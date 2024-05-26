package com.education.ekagratagkquiz.contribute_quiz.util

import android.net.Uri
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable

sealed class CreateQuizEvents {
    data class OnSubjectChanges(val subject: String) : CreateQuizEvents()
    data class ObDescChange(val desc: String) : CreateQuizEvents()
    data class OnUpdatedToSection(val isSection: Boolean) : CreateQuizEvents()/*True means section*/

    data class OnImageAdded(val uri: Uri? = null) : CreateQuizEvents()
    object OnImageRemoved : CreateQuizEvents()

    data class OnPdfAdded(val uri: Uri? = null) : CreateQuizEvents()
    object OnPdfRemoved : CreateQuizEvents()

    data class OnColorAdded(val color: ULong? = null) : CreateQuizEvents()

    data class OnSubmit(val quiz: QuizParcelable? = null) : CreateQuizEvents()
}
