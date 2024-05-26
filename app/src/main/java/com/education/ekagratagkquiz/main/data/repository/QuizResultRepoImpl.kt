package com.education.ekagratagkquiz.main.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.firebase_dto.QuizDto
import com.education.ekagratagkquiz.main.data.firebase_dto.QuizResultsDto
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import com.education.ekagratagkquiz.main.domain.repository.QuizResultsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuizResultRepoImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val user: FirebaseUser?,
    private val store: UserStore
) : QuizResultsRepository {

    override suspend fun getQuizResults(quizId:String): Flow<Resource<List<QuizResultModel?>>> {

        val quizDocPath = "/${FireStoreCollections.QUIZ_COLLECTION}/${quizId}"
        val documentRef = fireStore.document(quizDocPath)


        val query = fireStore.collection(FireStoreCollections.RESULT_COLLECTIONS).limit(30)
            .orderBy(FireStoreCollections.TOTAL_MARKS_FIELD, Query.Direction.DESCENDING)
            .whereEqualTo(FireStoreCollections.QUIZ_ID_FIELD, documentRef)


        Log.d("TAG", "getQuizResults: getResults query ${query.count()}")
        var job: Job? = null
        return callbackFlow {
            val callback = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("TAG", "getQuizResults: getResults error ${error.message}")
                    close()
                    return@addSnapshotListener
                }
                try {
                    Log.d("TAG", "getQuizResults: getResults: t doc size ${snapshot?.documents?.size}")
                    Log.d("TAG", "getQuizResults: getResults:  t doc  ${snapshot?.documents}")

                    val deferredValues = snapshot?.documents?.map { doc ->

                        Log.d("TAG", "getQuizResults: getResults: doc size ${doc.data?.size}")
                        Log.d("TAG", "getQuizResults: getResults: doc  ${doc.data}")

                        async {
                            val quidId = doc.data?.get(FireStoreCollections.QUIZ_ID_FIELD)

                            Log.d("TAG", "getQuizResults: getResults: id $quidId")

                            val quizData = (quidId as DocumentReference)
                                .get()
                                .await()
                                .toObject<QuizDto>()

                            Log.d("TAG", "getQuizResults: getResults: $quizData $quidId" +
                                    "\n" +
                                    "\ndocument ${doc.toObject<QuizResultsDto>()?.toModel()}" +
                                    "")
                            doc.toObject<QuizResultsDto>()
                                ?.copy(quizDto = quizData)
                                ?.toModel()
                        }
                    } ?: emptyList()

                    Log.d("TAG", "getQuizResults: getResults: job ${job}")

                    job = CoroutineScope(Dispatchers.IO).launch {
                        val data = deferredValues.awaitAll()
                        trySend(Resource.Success(data))
                    }
                } catch (e: FirebaseFirestoreException) {
                    e.printStackTrace()
                    trySend(Resource.Error(e.message ?: "FIREBASE ERROR"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    trySend(Resource.Error(e.message ?: "Unknown error"))
                }
            }
            awaitClose {
                job?.cancel()
                callback.remove()
            }
        }
    }

    override suspend fun getQuizResults(): Flow<Resource<List<QuizResultModel?>>> {
        val user = store.getUserDetail.first()

        /*whereEqualTo Refer to user Results.*/
        val query = fireStore.collection(FireStoreCollections.RESULT_COLLECTIONS).limit(30)
            .orderBy(FireStoreCollections.TOTAL_MARKS_FIELD, Query.Direction.DESCENDING)
//            .whereEqualTo(FireStoreCollections.USER_ID_FIELD, user.uid)

        var job: Job? = null
        return callbackFlow {
            val callback = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }
                try {
                    val deferredValues = snapshot?.documents?.map { doc ->
                        async {
                            val quidId = doc.data?.get(FireStoreCollections.QUIZ_ID_FIELD)

                            val quizData = (quidId as DocumentReference)
                                .get()
                                .await()
                                .toObject<QuizDto>()

                            Log.d("TAG", "getQuizResults: quizData: $quizData $quidId" +
                                    "\ndocument ${doc.data}" +
                                    "")
                            doc.toObject<QuizResultsDto>()
                                ?.copy(quizDto = quizData)
                                ?.toModel()
                        }
                    } ?: emptyList()
                    job = CoroutineScope(Dispatchers.IO).launch {
                        val data = deferredValues.awaitAll()
                        trySend(Resource.Success(data))
                    }
                } catch (e: FirebaseFirestoreException) {
                    trySend(Resource.Error(e.message ?: "FIREBASE ERROR"))
                } catch (e: Exception) {
                    trySend(Resource.Error(e.message ?: "Unknown error"))
                }
            }
            awaitClose {
                job?.cancel()
                callback.remove()
            }
        }
    }

    override suspend fun deleteQuizResults(resultId: String): Resource<Unit> {
        return try {
            fireStore
                .collection(FireStoreCollections.RESULT_COLLECTIONS)
                .document(resultId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Resource.Error(e.message ?: "FireStore exception")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Exception occurred")
        }
    }

}