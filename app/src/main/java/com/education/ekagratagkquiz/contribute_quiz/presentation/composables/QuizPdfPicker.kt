package com.education.ekagratagkquiz.contribute_quiz.presentation.composables

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.presentation.QuizViewModel
import com.education.ekagratagkquiz.contribute_quiz.util.CreateQuizEvents
import com.education.ekagratagkquiz.contribute_quiz.util.compressImage
import com.education.ekagratagkquiz.core.util.getFileName
import com.education.ekagratagkquiz.main.util.PreparationEvents

@Preview
@Composable
fun PreviewQuizPdfPicker(){
    QuizPdfPicker()
}

@Composable
fun QuizPdfPicker(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val pdf = viewModel.createQuiz.value.pdf

    val pdfFilePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onCreateQuizEvent(CreateQuizEvents.OnPdfAdded(uri = result.data?.data))
            }
        }

    Column(
        modifier = Modifier
            .padding(PaddingValues(top = 8.dp))
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
                    text = "Pick pdf for this Quiz",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.pdf_extra),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(2.dp)
                )
            }
            IconButton(
                onClick = { viewModel.onCreateQuizEvent(CreateQuizEvents.OnPdfRemoved) },
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveCircleOutline,
                    contentDescription = "Remove added pdf"
                )
            }
        }
        Box(
            modifier = modifier
                .padding(2.dp)
                .fillMaxWidth()
                .height(56.dp)
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
                    if (pdf == null) {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"  // Set the MIME type to filter files
                            //type = "application/vnd.ms-excel" // MIME type for Excel files
                        }
                        pdfFilePicker.launch(intent)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (pdf != null){
                val contentResolver = LocalContext.current.contentResolver
                val fileName = getFileName(contentResolver, pdf)
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Selected PDF: ")
                        }

                        if (fileName.isNotEmpty()) {
                            append(fileName)
                        } else {
                            append("Unknown")
                        }
                    }, style = MaterialTheme.typography.titleMedium
                )
            }
            else
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = "Pdf",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                    Text(
                        text = "Preparation Pdf",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

        }
    }
}