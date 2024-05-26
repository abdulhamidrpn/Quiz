package com.education.ekagratagkquiz.main.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.firebase_dto.QuizDto
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.domain.repository.HomeRepository
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class HomeRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : HomeRepository {
    val TAG = HomeRepository::class.java.simpleName
    override suspend fun getAllQuizzes(): Flow<Resource<List<QuizModel?>>> {

//        .whereEqualTo(FireStoreCollections.APPROVED_TAG, true)
        val colRef = fireStore
            .collection(FireStoreCollections.SECTION_COLLECTION)
            .orderBy(FireStoreCollections.TIMESTAMP_FIELD, Query.Direction.DESCENDING)
            .whereEqualTo(FireStoreCollections.SECTION_TAG, true)
        return callbackFlow {
            val callback = colRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TAG", "getAllQuizzes: FireStore error ${error.message}")
                    close()
                    return@addSnapshotListener
                }
                try {
                    val data = snapshot?.documents?.map { member ->
                        member.toObject<QuizDto>()?.toModel()
                    } ?: emptyList()
                    trySend(Resource.Success(data))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("TAG", "getAllQuizzes: error ${e.message}")
                    trySend(Resource.Error(e.message ?: "SOme exception occurred"))
                }
            }
            awaitClose { callback.remove() }
        }
    }

    override suspend fun getAllQuizzes(quiz: QuizParcelable): Flow<Resource<List<QuizModel?>>> {

        val ref = if (quiz.isSection) {
            if (quiz.path.isNullOrEmpty()) {
                fireStore.collection(FireStoreCollections.SECTION_COLLECTION)
            } else if (quiz.quizSize > 0) {
                fireStore.document(quiz.path).collection(FireStoreCollections.QUIZ_COLLECTION)
            } else {
                fireStore.document(quiz.path).collection(FireStoreCollections.SECTION_COLLECTION)
            }
        } else {
            if (quiz.path.isNullOrEmpty()) {
                fireStore.collection(FireStoreCollections.QUIZ_COLLECTION)
            } else {
                fireStore.document(quiz.path).collection(FireStoreCollections.QUIZ_COLLECTION)
            }
        }

        val colRef = ref
            .orderBy(FireStoreCollections.TIMESTAMP_FIELD, Query.Direction.DESCENDING)
        return callbackFlow {
            val callback = colRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TAG", "getAllQuizzes: FireStore error ${error.message}")
                    close()
                    return@addSnapshotListener
                }
                try {
                    val data = snapshot?.documents?.map { member ->
                        member.toObject<QuizDto>()?.toModel()
                    } ?: emptyList()
                    trySend(Resource.Success(data))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("TAG", "getAllQuizzes: error ${e.message}")
                    e.printStackTrace()
                    trySend(Resource.Error(e.message ?: "Error Occurred"))
                }
            }
            awaitClose { callback.remove() }
        }
    }

    override suspend fun deleteQuiz(quizPath: String?, quizId: String?): Flow<Resource<Boolean>> {
        return flow {
            try {
                val isQuiz = quizPath?.contains("/${FireStoreCollections.QUIZ_COLLECTION}") ?: false
                val isSection = !isQuiz

                /*Delete all quiz folder and section folder*/
                if (isSection && !quizPath.isNullOrEmpty()) {
                    val documentRef = fireStore.document(quizPath)
                    val documentRefQuiz = fireStore.document(quizPath)
                        .collection(FireStoreCollections.QUIZ_COLLECTION)
                    val documentRefSection =
                        fireStore.document(quizPath)
                            .collection(FireStoreCollections.SECTION_COLLECTION)

                    /*Delete all section under the parent section*/
                    val deleteResultSection = documentRefSection.get().await().documents
                        .map { snapshot ->
                            fireStore
                                .document(snapshot.reference.path)
                                .delete()
                                .asDeferred()
                        }
                    deleteResultSection.awaitAll()

                    /*Delete all quiz collection under that parent*/
                    val deleteResultQuiz = documentRefQuiz.get().await().documents
                        .map { snapshot ->
                            fireStore
                                .document(snapshot.reference.path)
                                .delete()
                                .asDeferred()
                        }
                    deleteResultQuiz.awaitAll()

                    /*Delete parent document*/
                    documentRef.delete().await()

                    emit(Resource.Success(true))
                } else if (quizId.isNullOrEmpty()) {
                    if (!quizPath.isNullOrEmpty()) {
                        fireStore.document(quizPath).delete().await()
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error("Could not found quiz path"))
                    }
                } else {
                    /*Delete all quiz and their Question*/
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

                    quizPath?.let { fireStore.document(it).delete().await() }

                    if (isQuiz) {
                        /*Update Quiz size on Section folder when delete any quiz.*/
                        val path =
                            quizPath?.split(FireStoreCollections.QUIZ_COLLECTION)?.firstOrNull()
                        val refLastSection = path?.let { fireStore.document(it) }
                        refLastSection?.update("quizSize", FieldValue.increment(-1))
                    }
                    emit(Resource.Success(true))
                }
            } catch (e: FirebaseFirestoreException) {
                emit(Resource.Error(e.message ?: "FIREBASE EXCEPTION"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "UNKNOWN EXCEPTION"))
            }
        }
    }
}