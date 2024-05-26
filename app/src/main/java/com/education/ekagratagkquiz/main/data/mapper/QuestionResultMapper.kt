package com.education.ekagratagkquiz.main.data.mapper

import com.google.firebase.firestore.FirebaseFirestore
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.core.firebase_paths.FireStoreCollections
import com.education.ekagratagkquiz.main.data.firebase_dto.CreateQuizResultsDto
import com.education.ekagratagkquiz.main.domain.models.CreateQuizResultModel
import com.education.ekagratagkquiz.main.domain.models.QuizResultModel

fun CreateQuizResultModel.toDto(
    fireStore: FirebaseFirestore,
    user: FirebaseUser
): CreateQuizResultsDto {
    return CreateQuizResultsDto(
        quizTitle = quizTitle,
        totalQuestions = totalQuestions,
        correctAnswer = correct,
        quizId = fireStore.document(FireStoreCollections.QUIZ_COLLECTION + "/$quizId"),
        userId = user.uid,
        userName = user.displayName
    )
}

fun QuizResultModel.toDto(
    fireStore: FirebaseFirestore,
    user: FirebaseUser
): CreateQuizResultsDto {
    return CreateQuizResultsDto(
        attemptedAnswer = attempt,
        totalQuestions = totalQuestions,
        correctAnswer = correct,
        totalMarks = finalResult,
        quizId = fireStore.document(FireStoreCollections.QUIZ_COLLECTION + "/$quizId"),
        userId = user.uid,
        userName = user.displayName,
        quizTitle = quizTitle
    )
}