package com.education.ekagratagkquiz.main.util

import com.education.ekagratagkquiz.main.domain.models.PreparationModel

sealed class PreparationInteractionEvents {
    data class PdfSelected(val preparation: PreparationModel) : PreparationInteractionEvents()
    object PdfUnselect : PreparationInteractionEvents()
}