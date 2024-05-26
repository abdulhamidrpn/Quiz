package com.education.ekagratagkquiz.contribute_quiz.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.QuestionsRepository
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.firebase_dto.QuestionsDto
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import org.apache.poi.ss.usermodel.WorkbookFactory
import javax.inject.Inject

class QuestionRepoImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : QuestionsRepository {
    val TAG = QuestionRepoImpl::class.java.simpleName


    override suspend fun getQuestions(quiz: String): Flow<Resource<List<QuestionModel?>>> {
        val docPath = "/" + FireStoreCollections.QUIZ_COLLECTION + "/" + quiz
        Log.d(TAG, "getQuestions: docPath: $docPath")
        val query = fireStore
            .collection(FireStoreCollections.QUESTION_COLLECTION)
            .whereEqualTo(
                FireStoreCollections.QUIZ_ID_FIELD,
                fireStore.document(docPath)
            )
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
                            snap?.documents?.map { snapshot -> snapshot.toObject<QuestionsDto>() }
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

    override suspend fun deleteQuestion(questionModel: QuestionModel): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                fireStore
                    .collection(FireStoreCollections.QUESTION_COLLECTION)
                    .document(questionModel.uid)
                    .delete()
                    .await()
                emit(Resource.Success(true))
            } catch (e: FirebaseFirestoreException) {
                emit(Resource.Error(e.message ?: "Firebase exception occurred"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown exception occurred "))
            }
        }
    }

    override suspend fun deleteQuiz(quizId: String): Flow<Resource<Boolean>> {
        return flow {
            try {
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

                emit(Resource.Success(true))
            } catch (e: FirebaseFirestoreException) {
                emit(Resource.Error(e.message ?: "FIREBASE EXCEPTION"))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "UNKNOWN EXCEPTION"))
            }
        }
    }
}