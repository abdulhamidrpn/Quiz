package com.education.ekagratagkquiz.main.presentation.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.education.ekagratagkquiz.main.util.TabsInfo
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun AppBottomBarPreview() {
    AppBottomNav(pager = rememberPagerState {
        3
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppBottomNav(
    pager: PagerState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val items = remember {
        listOf(
            TabsInfo.HomeTab,
            TabsInfo.ResultsTab,
            TabsInfo.ProfileTab
        )
    }

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { tabs ->
            NavigationBarItem(
                selected = pager.currentPage == tabs.index,
                onClick = { scope.launch { pager.scrollToPage(tabs.index) } },
                label = { Text(text = tabs.title) },
                icon = {
                    if (pager.currentPage == tabs.index)
                        Icon(tabs.icon, contentDescription = tabs.desc)
                    else
                        Icon(tabs.outlineIcon, contentDescription = tabs.desc)
                },
            )
        }
    }
}
