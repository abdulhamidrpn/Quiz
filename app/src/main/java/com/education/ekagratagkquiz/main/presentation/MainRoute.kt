package com.education.ekagratagkquiz.main.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.main.presentation.composables.AppBottomNav
import com.education.ekagratagkquiz.main.presentation.composables.QuizTopBar
import com.education.ekagratagkquiz.main.presentation.screens.HomeScreen
import com.education.ekagratagkquiz.main.presentation.screens.PreparationScreen
import com.education.ekagratagkquiz.main.presentation.screens.ProfileScreen
import com.education.ekagratagkquiz.main.presentation.screens.ResultsScreen


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(
    navController: NavController,
    modifier: Modifier = Modifier,
    widthSizeClass: WindowWidthSizeClass,
) {

    val isExpandedScreen = (widthSizeClass == WindowWidthSizeClass.Expanded || widthSizeClass == WindowWidthSizeClass.Medium)

    val pager = rememberPagerState(initialPage = 0) {
        3
    }
/*
    // TopAppBar Scrollable adding modifier on scaffold it will move with page scroll collapsing toolbar.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    */
    Scaffold(
        bottomBar = { AppBottomNav(pager = pager) },
        topBar = { QuizTopBar(pager = pager) },
    ) { padding ->

        val preparationViewModel = hiltViewModel<PreparationViewModel>()

        HorizontalPager(state = pager, contentPadding = padding, modifier = modifier) { idx ->
            when (idx) {
                0 -> HomeScreen(navController = navController, isExpandedScreen = isExpandedScreen)
                /*1 -> PreparationScreen(
                    navController = navController,
                    viewModel = preparationViewModel,
                    preparationState = preparationViewModel.preparationState.value)*/

                1 -> ResultsScreen(navController = navController, isExpandedScreen = isExpandedScreen)
                2 -> ProfileScreen()
//                1 -> AllQuizzesScreen(navController = navController)
//                2 -> QuizContributionScreen(navController = navController)
//                3 -> ProfileScreen()
            }
        }
    }
}
