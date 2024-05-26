package com.education.ekagratagkquiz.main.domain.repository

import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.domain.models.PreparationModel
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import kotlinx.coroutines.flow.Flow

interface PreparationRepository {
    suspend fun getAllPdfs(): Flow<Resource<List<PreparationModel?>>>

    suspend fun updatePdf(
        preparationModel: PreparationModel
    ): Flow<Resource<Unit>>

    suspend fun uploadPdf(path: String): String

    suspend fun deletePdf(path: String? = "", id: String? = ""): Flow<Resource<Boolean>>
}