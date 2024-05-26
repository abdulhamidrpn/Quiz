package com.education.ekagratagkquiz.contribute_quiz.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.education.ekagratagkquiz.contribute_quiz.data.mappers.toDto
import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuizModel
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuizRepository
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.firebase_paths.StoragePaths
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.firebase_dto.QuizDto
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CreateQuizRepoImpl @Inject constructor(
    private val user: FirebaseUser?,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
) : CreateQuizRepository {
    val TAG = CreateQuizRepoImpl::class.java.simpleName
    override suspend fun createQuiz(quiz: CreateQuizModel): Flow<Resource<QuizModel?>> {

        val TAG = CreateQuizRepoImpl::class.java.simpleName

        return flow {
            try {
                emit(Resource.Loading())

                Log.d(TAG, "createQuiz: ${quiz.toString()}")


                val refSection = if (quiz.path.isNullOrEmpty()) {
                    fireStore.collection(FireStoreCollections.SECTION_COLLECTION)
                } else
                    fireStore.document(quiz.path)
                        .collection(FireStoreCollections.SECTION_COLLECTION)

                val refQuizzes = if (quiz.path.isNullOrEmpty()) {
                    fireStore.collection(FireStoreCollections.QUIZ_COLLECTION)
                } else
                    fireStore.document(quiz.path).collection(FireStoreCollections.QUIZ_COLLECTION)


                val ref = if (quiz.isSection) {
                    refSection
                } else {
                    refQuizzes
                }


                val data = ref
                    .add(quiz.toDto())
                    .await()
                Log.d(TAG, "createQuiz: reference is ${ref.path}/${data.id}")

                if (!quiz.isSection) {
                    /*Update Quiz size on Section folder when add any quiz.*/
                    val path = quiz.path?.split(FireStoreCollections.QUIZ_COLLECTION)?.firstOrNull()
                    val refLastSection = path?.let { fireStore.document(it) }
                    refLastSection?.update("quizSize", FieldValue.increment(1))
                }
                /*Update quiz path to each quiz or section to track of folder.*/
                ref.document(data.id).update("path", "${ref.path}/${data.id}").await()

                val quizDto = data
                    .get()
                    .await()
                    .toObject<QuizDto>()


                emit(Resource.Success(quizDto?.toModel()))
            } catch (e: FirebaseFirestoreException) {
                e.printStackTrace()
                emit(Resource.Error(message = e.message ?: "FireStore message error"))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(message = e.message ?: "Unknown error"))
            }
        }
    }

    override suspend fun uploadQuizImage(path: String): String {
        val uri = Uri.parse(path)
        val pathString = "${user!!.uid}/${StoragePaths.QUIZ_PATH}/${uri.lastPathSegment}"
        val fileRef = storage.reference.child(pathString)
        val task = fileRef.putFile(uri).await()
        val outUri = task.storage.downloadUrl.await()
        return outUri.toString()
    }

    override suspend fun uploadQuizPdf(path: String): String {
        val uri = Uri.parse(path)
        val pathString = "${StoragePaths.PDF_PATH}/${uri.lastPathSegment}"
        val fileRef = storage.reference.child(pathString)
        val task = fileRef.putFile(uri).await()
        val outUri = task.storage.downloadUrl.await()
        return outUri.toString()
    }

    override suspend fun deleteQuiz(quizPath: String, quizId: String?): Flow<Resource<Boolean>> {
        return flow {
            try {
                if (quizId.isNullOrEmpty()){
                    fireStore.document(quizPath).delete().await()
                    emit(Resource.Success(true))
                }else {
                    Log.d(TAG, "deleteQuiz: QuizPath $quizPath")
                    val quizDocPath = "/${FireStoreCollections.QUIZ_COLLECTION}/${quizId}"
                    val documentRef = fireStore.document(quizDocPath)
                    val query = fireStore
                        .collection(FireStoreCollections.QUESTION_COLLECTION)
                        .whereEqualTo(FireStoreCollections.QUIZ_ID_FIELD, documentRef)
                    val data = query.get().await()

                    val deleteQuery = data.documents
                        .map { snapshot ->
                            fireStore
                                .document(snapshot.reference.path)
                                .delete()
                                .asDeferred()
                        }
                    deleteQuery.awaitAll()
                    val resultQuery = fireStore
                        .collection(FireStoreCollections.RESULT_COLLECTIONS)
                        .whereEqualTo(FireStoreCollections.QUIZ_ID_FIELD, documentRef)
                    val resultData = resultQuery.get().await()

                    val deleteResult = resultData.documents
                        .map { snapshot ->
                            fireStore
                                .document(snapshot.reference.path)
                                .delete()
                                .asDeferred()
                        }
                    deleteResult.awaitAll()

                    fireStore.document(quizPath).delete().await()

                    emit(Resource.Success(true))
                }
            } catch (e: FirebaseFirestoreException) {
                emit(Resource.Error(e.message ?: "FIREBASE EXCEPTION"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "UNKNOWN EXCEPTION"))
            }
        }
    }

    private fun updateValue(docRef: DocumentReference) {
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("TAG", "updateValue: ${documentSnapshot.data?.values}")
                    val currentValue =
                        documentSnapshot.getLong("quizSize") ?: 0L // Handle potential missing value
                    val newValue = currentValue + 1 // Increment locally

                    docRef.update("quizSize", newValue)
                        .addOnSuccessListener { println("Value incremented successfully!") }
                        .addOnFailureListener { exception ->
                            println("Error updating value: $exception")
                            docRef.set(hashMapOf("quizSize" to newValue))
                                .addOnSuccessListener { println("Value incremented successfully!") }
                        }
                } else {
                    println("Document does not exist")
                }
            }
            .addOnFailureListener { exception -> println("Error getting document: $exception") }
    }
}