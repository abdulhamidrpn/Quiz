package com.education.ekagratagkquiz.core.util

object NavParams {
    const val SECTION_ID: String = "sectionId"
    const val SECTION_PARAM_ID: String = "/{$SECTION_ID}"
    const val QUIZ_ID: String = "quizId"
    const val QUIZ_PARAM_ID: String = "/{$QUIZ_ID}"

    const val QUIZ_OR_SECTION_PARAM_ID: String = "?$SECTION_ID={$SECTION_ID}&$QUIZ_ID={$QUIZ_ID}"

    const val QUIZ_TAG: String = "quiz"
    const val SOURCE_VALID_ID: String = "valid"
    const val SOURCE_VALID_PARAMS_ID: String = "?valid={$SOURCE_VALID_ID}"
}