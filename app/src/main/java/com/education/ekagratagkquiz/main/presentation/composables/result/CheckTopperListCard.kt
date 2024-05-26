package com.education.ekagratagkquiz.main.presentation.composables.result

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewCheckTopperListCard() {
    CheckTopperListCard(onToggle = {})
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun CheckTopperListCard(
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {

    Card(
        modifier = modifier
            .clickable(
                onClick = { onToggle() }
            )
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.weight(.20f),
                imageVector = Icons.Filled.Dataset,
                contentDescription = "Checkout Results"
            )

            Column(
                modifier = Modifier.weight(.6f)
            ) {
                Text(text = "Check Others Result", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Don't compare with others but get idea about others result.",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Icon(
                modifier = Modifier.weight(.20f),
                imageVector = Icons.Filled.ArrowOutward,
                contentDescription = "Go to Result on Quiz"
            )
        }

    }
}
