package com.education.ekagratagkquiz.main.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

data class WindowSize(
    val width: WindowType,
    val height: WindowType
)

enum class WindowType { Compact, Medium, Expanded }

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidth by remember(key1 = configuration) {
        mutableStateOf(configuration.screenWidthDp)
    }
    val screenHeight by remember(key1 = configuration) {
        mutableStateOf(configuration.screenHeightDp)
    }

    return WindowSize(
        width = getScreenWidth(screenWidth),
        height = getScreenHeight(screenHeight)
    )
}

fun getScreenWidth(width: Int): WindowType = when {
    width < 600 -> WindowType.Compact
    width < 840 -> WindowType.Medium
    else -> WindowType.Expanded
}

fun getScreenHeight(height: Int): WindowType = when {
    height < 480 -> WindowType.Compact
    height < 900 -> WindowType.Medium
    else -> WindowType.Expanded
}


@Composable
fun rememberScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidthDp = with(LocalDensity.current) { configuration.screenWidthDp.dp }
    val screenHeightDp = with(LocalDensity.current) { configuration.screenHeightDp.dp }

    val screenSize = when {
        screenWidthDp >= 600.dp && screenHeightDp >= 600.dp -> ScreenSize.Large
        screenWidthDp >= 600.dp -> ScreenSize.Wide
        else -> ScreenSize.Normal
    }

    return screenSize
    /*when (screenSize) {
        ScreenSize.Large -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Large Screen")
            }
        }
        ScreenSize.Wide -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Wide Screen")
            }
        }
        ScreenSize.Normal -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Normal Screen")
            }
        }
    }*/
}

enum class ScreenSize {
    Large,
    Wide,
    Normal
}