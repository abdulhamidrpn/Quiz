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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.education.ekagratagkquiz.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewTelegramCard() {
    TelegramCard()
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun TelegramCard(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {

    Card(
        modifier = modifier
            .clickable(
                onClick = {
                    val telegramUrl = "https://t.me/egqlifescience" // Replace with your channel ID
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    context.startActivity(intent)
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

            Image(
                modifier = Modifier.weight(.20f),
                painter = painterResource(id = R.drawable.telegram),
                contentDescription = "Go to Telegram channel"
            )

            Column(
                modifier = Modifier.weight(.6f)
            ) {
                Text(text = "Connect With Us", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Get update about apps in telegram.",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Icon(
                modifier = Modifier.weight(.20f),
                imageVector = Icons.Filled.ArrowOutward,
                contentDescription = "Go to Telegram channel"
            )
        }

    }
}
