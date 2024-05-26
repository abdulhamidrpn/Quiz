package com.education.ekagratagkquiz.main.util

import android.net.Uri

data class PreparationState(
    val fileName: String = "",
    val fileUri: String? = null,
    val isRequestPermission: Boolean = false,
    val isError: Boolean = false,

    val loading: Boolean = false,
    val selectedDocumentUri: Uri? = null
)
sealed class PreparationEvents {
    data class readFile(val uri: Uri? = null) : PreparationEvents()
    data class OnDeleteConfirmed(val quizPath: String?) : PreparationEvents()
    object OnDeleteCanceled : PreparationEvents()
    object OnRequestPermission : PreparationEvents()
}
