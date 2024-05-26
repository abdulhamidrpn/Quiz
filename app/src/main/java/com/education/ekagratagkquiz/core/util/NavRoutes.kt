package com.education.ekagratagkquiz.core.util

import com.education.ekagratagkquiz.core.util.NavParams.QUIZ_OR_SECTION_PARAM_ID

sealed class NavRoutes(val route: String) {
    object NavHomeRoute : NavRoutes("home")
    object NavCreateQuizRoute : NavRoutes(route = "create_quiz") {
        fun passQuizOrSectionId(quizId: String? = null, sectionId: String? = null): String {
            return "$route?sectionId={${NavParams.SECTION_ID}}&quizId={${NavParams.QUIZ_ID}}"
        }
    }

    object NavPreparationRoute : NavRoutes(route = "preparation")
    object NavResultsRoute : NavRoutes(route = "results")

    object NavDetailsRoute : NavRoutes("/details")
    object NavQuizRoute : NavRoutes("/quiz")
    object NavAddQuestionsRoute : NavRoutes("/add-questions")
    object NavViewQuestions : NavRoutes("/view-questions")
}
