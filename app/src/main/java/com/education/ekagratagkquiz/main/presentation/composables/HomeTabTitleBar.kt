package com.education.ekagratagkquiz.main.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle

@Composable
fun HomeTabTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    arrangementStyle: QuizArrangementStyle = QuizArrangementStyle.GridStyle,
    onListStyle: () -> Unit,
    onGridStyle: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        when (arrangementStyle) {
            is QuizArrangementStyle.ListStyle -> IconButton(
                onClick = onGridStyle,
                colors = IconButtonDefaults
                    .iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = "List style arrangement"
                )
            }
            is QuizArrangementStyle.GridStyle -> IconButton(
                onClick = onListStyle, colors = IconButtonDefaults
                    .iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "Grid Style"
                )
            }
        }
    }
}