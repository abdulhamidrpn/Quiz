package com.education.ekagratagkquiz.profile.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.profile.presentation.UserNameFieldState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeUserNameSettings(
    state: UserNameFieldState,
    user: FirebaseUser?,
    onToggle: () -> Unit,
    onSubmit: () -> Unit,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isDialogOpen) {
        AlertDialog(
            onDismissRequest = onToggle,
            confirmButton = {
                Button(onClick = onSubmit) { Text(text = "Change") }
            },
            dismissButton = {
                TextButton(onClick = onToggle) { Text(text = "Cancel") }
            },
            title = { Text(text = "Change UserName") },
            text = {
                Column(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    TextField(
                        value = state.name,
                        isError = state.error != null,
                        onValueChange = onChange,
                        placeholder = { Text(text = "New username") },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        shape = MaterialTheme.shapes.medium,
                    )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(16.dp)
                    ) {
                        state.error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        )
    }
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(.75f)
            ) {
                Text(text = "Change UserName", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Don't like the current name,change it if so.",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            TextButton(
                onClick = onToggle,
                modifier = Modifier.weight(.25f)
            ) {
                Text(text = if (user?.displayName != null) "Change" else "Set")
            }
        }

    }
}