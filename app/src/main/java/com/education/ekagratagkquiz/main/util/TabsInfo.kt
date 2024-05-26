package com.education.ekagratagkquiz.main.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabsInfo(
    val title: String,
    val icon: ImageVector,
    val outlineIcon: ImageVector,
    val desc: String? = null,
    val index: Int
) {
    object HomeTab : TabsInfo(
        title = "Home",
        icon = Icons.Default.Home,
        outlineIcon = Icons.Outlined.Home,
        desc = "Home Tab",
        index = 0
    )

    object PreparationTab : TabsInfo(
        title = "Preparation",
        icon = Icons.Default.PictureAsPdf,
        outlineIcon = Icons.Outlined.PictureAsPdf,
        desc = "Preparation",
        index = 3
    )

    object ResultsTab : TabsInfo(
        title = "Results",
        icon = Icons.Default.Dataset,
        outlineIcon = Icons.Outlined.Dataset,
        desc = "Results Tab",
        index = 1
    )

    object ProfileTab : TabsInfo(
        title = "Profile",
        icon = Icons.Default.Person,
        outlineIcon = Icons.Outlined.Person,
        desc = "profile",
        index = 2
    )
}
