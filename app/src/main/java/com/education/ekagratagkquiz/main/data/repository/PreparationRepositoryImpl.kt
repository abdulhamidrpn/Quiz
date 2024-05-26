package com.education.ekagratagkquiz.main.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.education.ekagratagkquiz.contribute_quiz.data.mappers.toDto
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.firebase_paths.StoragePaths
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.firebase_dto.PreparationDto
import com.education.ekagratagkquiz.main.data.firebase_dto.QuizDto
import com.education.ekagratagkquiz.main.domain.models.PreparationModel
import com.education.ekagratagkquiz.main.domain.repository.HomeRepository
import com.education.ekagratagkquiz.main.domain.repository.PreparationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class PreparationRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    private val store: UserStore,
    @ApplicationContext private val context: Context
) : PreparationRepository {
    val TAG = HomeRepository::class.java.simpleName

    override suspend fun getAllPdfs(): Flow<Resource<List<PreparationModel?>>> {

        val query = fireStore.collection(FireStoreCollections.PDFS_COLLECTION)

        return callbackFlow {
            trySend(Resource.Loading())
            val callback = query
                .addSnapshotListener { snap, error ->
                    if (error != null) {
                        Log.d(TAG, "getQuestions: ${error.message}")
                        close()
                        return@addSnapshotListener
                    }
                    try {
                        Log.d(TAG, "getQuestions: ${snap?.documents?.size} ${snap?.documents}")
                        val questions =
                            snap?.documents?.map { snapshot ->
                                snapshot.toObject<PreparationDto>()
                            }
                                ?.map { it?.toModel() } ?: emptyList()
                        trySend(Resource.Success(questions))
                    } catch (e: Exception) {
                        Log.e(TAG, "getQuestions: Error ${e.message}")
                        trySend(Resource.Error(message = e.message ?: "Unknown error"))
                    }
                }
            awaitClose { callback.remove() }
        }
    }


    override suspend fun updatePdf(
        preparationModel: PreparationModel
    ): Flow<Resource<Unit>> {
        val collPath = fireStore.collection(FireStoreCollections.PDFS_COLLECTION)

        return flow {
            emit(Resource.Loading())
            try {

                val data = collPath
                    .add(preparationModel.toDto())
                    .await()
                Log.d(TAG, "createQuiz: reference is ${collPath.path}/${data.id}")


                /*val quizDto = data
                    .get()
                    .await()
                    .toObject<QuizDto>()

                Log.d(TAG, "updatePdf: uploadPdf: success $quizDto")*/

                emit(Resource.Success(Unit))
            } catch (e: FirebaseFirestoreException) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "FIREBASE ERROR OCCURRED"))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "EXCEPTION OCCURRED"))
            }

        }
    }

    override suspend fun uploadPdf(path: String): String {
        val uri = Uri.parse(path)
        val pathString = "${StoragePaths.PDF_PATH}/${uri.lastPathSegment}"
        val fileRef = storage.reference.child(pathString)
        val task = fileRef.putFile(uri).await()
        val outUri = task.storage.downloadUrl.await()
        return outUri.toString()
    }

    override suspend fun deletePdf(path: String?, id: String?): Flow<Resource<Boolean>> {
        TODO("Delete uploaded pdf or delete this function.")
    }

}