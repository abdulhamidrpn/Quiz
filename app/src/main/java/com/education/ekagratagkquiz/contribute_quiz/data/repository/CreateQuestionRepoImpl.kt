package com.education.ekagratagkquiz.contribute_quiz.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.education.ekagratagkquiz.contribute_quiz.data.mappers.toDto
import com.education.ekagratagkquiz.contribute_quiz.domain.model.CreateQuestionsModel
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.contribute_quiz.domain.repository.CreateQuestionsRepo
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.core.firebase_paths.StoragePaths
import com.education.ekagratagkquiz.core.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import javax.inject.Inject

class CreateQuestionRepoImpl @Inject constructor(
    private val user: FirebaseUser?,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : CreateQuestionsRepo {

    val TAG = CreateQuestionsRepo::class.java.simpleName


    override suspend fun readExcelData(
        uri: Uri,
        quizId: String
    ): Flow<Resource<List<CreateQuestionsModel?>>> {

        val inputStream = context.contentResolver.openInputStream(uri)
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val questions = mutableListOf<CreateQuestionsModel?>()

        return channelFlow {
            withContext(Dispatchers.IO) { // Use IO dispatcher for blocking operations
                try {
                    for (rowIndex in 1 until sheet.physicalNumberOfRows) {
                        val row = sheet.getRow(rowIndex)
                        val question = row.getCell(0).toString()
                        val description = row.getCell(1).toString()
                        val explanation = row.getCell(2).toString()

                        val options: MutableList<QuestionOption> = mutableListOf()
                        for (cellIndex in 3 until row.physicalNumberOfCells step 2) {
                            val cellOption = row.getCell(cellIndex).toString()
                            val cellSelected = row.getCell(cellIndex + 1).toString().toBoolean()
                            if (cellOption.isNotEmpty())
                                options.add(QuestionOption(cellOption, cellSelected))
                        }


                        val questionModel = CreateQuestionsModel(
                            isSourceExcel = true,
                            quizId = quizId,
                            question = question,
                            description = description.ifEmpty { null },
                            questionExplanation = explanation.ifEmpty { null },
                            options = options
                        )
                        questions.add(questionModel)

                        Log.d(TAG, "readExcelData: row $rowIndex options: $questionModel")
                    }
                    send(Resource.Success(questions))
                    Log.d(TAG, "readData: success")
                } catch (e: Exception) {
                    send(Resource.Error(message = e.message.toString()))
                    Log.e(TAG, "readData: error ${e.message}")
                } finally {
                    inputStream?.close() // Close the stream even if an exception occurs
                    Log.d(TAG, "readData: inputStream closed")
                }
            }
        }
    }

    override suspend fun createQuestionsToQuiz(
        questions: List<CreateQuestionsModel>
    ): Flow<Resource<Unit>> {
        val collPath = fireStore
            .collection(FireStoreCollections.QUESTION_COLLECTION)

        return flow {
            emit(Resource.Loading())
            try {
                questions
                    .map { model -> model.toDto(fireStore) }
                    .map { dto -> collPath.add(dto).asDeferred() }
                    .awaitAll()
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

    override suspend fun uploadImage(path: String): String {

        val uri = Uri.parse(path)
        val pathString = "${user!!.uid}/${StoragePaths.QUESTION_PATH}/${uri.lastPathSegment}"
        val fileRef = storage.reference.child(pathString)
        val task = fileRef.putFile(uri).await()
        val outUri = task.storage.downloadUrl.await()
        return outUri.toString()
    }

}
