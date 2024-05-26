package com.education.ekagratagkquiz.main.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.util.Resource
import com.education.ekagratagkquiz.main.data.firebase_dto.QuestionsDto
import com.education.ekagratagkquiz.main.data.firebase_dto.QuizDto
import com.education.ekagratagkquiz.main.data.mapper.toDto
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel
import com.education.ekagratagkquiz.main.domain.repository.FullQuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FullQuizRepoImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val store: UserStore
) : FullQuizRepository {
    val TAG = FullQuizRepoImpl::class.java.simpleName

    override suspend fun getAllQuestions(quiz: String): Flow<Resource<List<QuestionModel?>>> {
        val docPath = "/" + FireStoreCollections.QUIZ_COLLECTION + "/" + quiz
        return flow {
            try {
                val query =
                    fireStore
                        .collection(FireStoreCollections.QUESTION_COLLECTION)
                        .whereEqualTo(
                            FireStoreCollections.QUIZ_ID_FIELD,
                            fireStore.document(docPath)
                        )
                        .get()
                        .await()
                val questions = query.documents.map { snapshot ->
                    snapshot.toObject<QuestionsDto>()?.toModel()
                }
                emit(Resource.Success(questions))
            } catch (e: FirebaseFirestoreException) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Firebase Exception occurred"))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Unknown exception occurred"))
            }
        }
    }

    override suspend fun getCurrentQuiz(uid: String): Resource<QuizModel?> {
        try {
            val quizDto =
                fireStore.collection(FireStoreCollections.QUIZ_COLLECTION).document(uid).get()
                    .await()
            if (!quizDto.exists()) return Resource.Error(message = "The quiz with $uid not found")
            val quizModel = quizDto.toObject<QuizDto>()?.toModel()
            if (quizModel?.isApproved == false) return Resource.Error(message = "The quiz is not approved contact admin")
            return Resource.Success(quizModel)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            return Resource.Error(e.message ?: "Firebase exception occurred")
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(e.message ?: "Exception occurred")
        }

    }

    override suspend fun setResult(result: QuizResultModel): Flow<Resource<Boolean>> {

        Log.d(TAG, "setResult: ")
        val user = store.getUserDetail.first()

        val dto = result.toDto(fireStore, user)
        val query = fireStore.collection(FireStoreCollections.RESULT_COLLECTIONS)
            .whereEqualTo(FireStoreCollections.QUIZ_ID_FIELD, dto.quizId)
            .whereEqualTo(FireStoreCollections.USER_ID_FIELD, user.uid)
        return channelFlow {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "setResult: queriedData fetching")
//                val queriedData = query.get().await()
                    val queriedData = try {
                        Log.d(TAG, "setResult: queriedData fetched await()")
                        query.get().await()
                    } catch (e: Exception) {
                        Log.d(TAG, "setResult: queriedData fetched null ${e.message}")
                        null
                    }
                    if (queriedData?.documents?.isNotEmpty() == true) {
                        Log.d(TAG, "setResult: queriedData: ${queriedData.documents.size}")
                        Log.d(TAG, "setResult: queriedData: ${queriedData.documents}")
                        val doc = queriedData.documents.first()
                        if (doc.exists()) {
                            fireStore.collection(FireStoreCollections.RESULT_COLLECTIONS)
                                .document(doc.id).update(dto.updateMap()).await()
                        }
                    } else {
                        Log.d(TAG, "setResult: else add ${queriedData?.documents?.size}")
                        fireStore.collection(FireStoreCollections.RESULT_COLLECTIONS).add(dto)
                            .await()
                    }
                    send(Resource.Success(true))
                } catch (e: FirebaseFirestoreException) {
                    e.printStackTrace()
                    send(Resource.Error(e.message ?: "FireStore exception"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    send(Resource.Error(e.message ?: "Unknown error "))
                }
            }
        }
    }
}