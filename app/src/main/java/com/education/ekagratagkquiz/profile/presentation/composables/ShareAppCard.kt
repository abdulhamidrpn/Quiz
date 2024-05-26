package com.education.ekagratagkquiz.profile.presentation.composables

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.education.ekagratagkquiz.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewShareAppCard() {
    ShareAppCard()
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun ShareAppCard(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {

    val subject = stringResource(id = R.string.app_name)
    Card(
        modifier = modifier
            .clickable(
                onClick = {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject )
                        var shareMessage =
                            "\nPrep for your biology exams with EGQ LIFE SCIENCE APP! Practice questions, PYQs, and Mock tests all in one place. \n\n\n"
                        shareMessage =
                            """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${context.packageName}
                    
                    
                    """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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
                imageVector = Icons.Filled.Quiz,
                contentDescription = "Go to Telegram channel"
            )

            Column(
                modifier = Modifier.weight(.6f)
            ) {
                Text(text = "Share With", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Let your friends know about this.",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Icon(
                modifier = Modifier.weight(.20f),
                imageVector = Icons.Filled.Share,
                contentDescription = "Go to Telegram channel"
            )
        }

    }
}
