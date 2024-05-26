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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.contribute_quiz.util.compressImage

@Preview
@Composable
fun PreviewOptionImage() {
    OptionImage()
}

@Preview
@Composable
fun PreviewOptionImagePicker() {
    OptionImagePicker(onImageChanged = {}, onImageOptionRemoved = {})
}

@Composable
fun OptionImagePicker(
    modifier: Modifier = Modifier,
    image: String = "",
    optionIndex: Int = 0,
    onImageChanged: (String) -> Unit,
    onImageOptionRemoved: () -> Unit,
    context: Context = LocalContext.current
) {
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            compressImage(context, uri, 80)
                .thenAccept { compressedImageUri ->
                    // Use the compressedImageUri for further processing (e.g., upload)
                    onImageChanged(compressedImageUri.toString())
                }
                .exceptionally { error ->
                    // Handle exception (e.g., compression failed)
                    println("Error compressing image: ${error.message}")
                    onImageChanged(uri.toString())
                    null // Indicate error (optional)
                }
        }
    Column(
        modifier = Modifier
            .padding(PaddingValues(top =  8.dp))
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
            .padding(all = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Option $optionIndex",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(0.9f)
            )

            IconButton(
                onClick = { onImageOptionRemoved() },
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
//                    if (image.isEmpty()) {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                    }
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

@Composable
fun OptionImage(
    modifier: Modifier = Modifier,
    image: String = "",
    optionIndex: Int = 0,
    context: Context = LocalContext.current
) {
    Column(
        modifier = Modifier.padding(PaddingValues(top = 8.dp))
    ) {
        Text(
            text = "Option $optionIndex",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium
        )
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