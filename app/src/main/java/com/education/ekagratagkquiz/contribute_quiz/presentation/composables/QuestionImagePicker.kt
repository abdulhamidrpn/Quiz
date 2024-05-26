package com.education.ekagratagkquiz.contribute_quiz.presentation.composables

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuestionState
import com.education.ekagratagkquiz.contribute_quiz.util.compressImage

@Preview
@Composable
fun PreviewQuestionImagePicker() {

}

@Composable
fun QuestionImagePicker(
    question: CreateQuestionState,
    modifier: Modifier = Modifier,
    onQuestionChanged: (String) -> Unit,
    context: Context = LocalContext.current
) {
    val image = question.question
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            compressImage(context, uri, 80)
                .thenAccept { compressedImageUri ->
                    // Use the compressedImageUri for further processing (e.g., upload)
                    onQuestionChanged(compressedImageUri.toString())
                }
                .exceptionally { error ->
                    // Handle exception (e.g., compression failed)
                    println("Error compressing image: ${error.message}")
                    onQuestionChanged(uri.toString())
                    null // Indicate error (optional)
                }
        }
    Column(
        modifier = Modifier.padding(PaddingValues(top = 8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.9f)
            ) {
                Text(
                    text = "Pick image",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.image_extra_question),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(2.dp)
                )
            }
            IconButton(
                onClick = { onQuestionChanged("") },
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveCircleOutline,
                    contentDescription = "Remove added image"
                )
            }
        }
        Box(
            modifier = modifier
                .aspectRatio(1.5f)
                .padding(horizontal = 2.dp, vertical = 16.dp)
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        ),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }
                .clickable {
                    if (image.isEmpty()) {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (image.isNotEmpty())
                AsyncImage(
                    model = ImageRequest.Builder(context).data(image).build(),
                    contentDescription = "User photo",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .aspectRatio(1.5f)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                )
            else
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Image",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                    Text(
                        text = "Aspect Ratio = 3:2",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

        }
    }
}