package com.education.ekagratagkquiz.main.presentation.composables

import android.content.Context
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.main.domain.models.QuizModel
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import java.time.format.DateTimeFormatter
import android.graphics.Color as Parser

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuizCard(
    quiz: QuizModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    arrangement: QuizArrangementStyle = QuizArrangementStyle.GridStyle,
    context: Context = LocalContext.current,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        border = BorderStroke(2.dp, Color(Parser.parseColor(quiz.color))),
        colors = CardDefaults.cardColors(
            containerColor = Color(Parser.parseColor(quiz.color))
                .copy(alpha = if (darkTheme) 0.1f else 1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            when (arrangement) {
                QuizArrangementStyle.GridStyle -> {
                    if (quiz.image != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(quiz.image).build(),
                            contentDescription = "Quiz image url :${quiz.image}",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = quiz.subject,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    quiz.desc?.let { desc ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                QuizArrangementStyle.ListStyle -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(.75f)
                        ) {
                            Text(
                                text = quiz.subject,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            quiz.desc?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 3,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                        if (quiz.image != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(quiz.image).build(),
                                contentDescription = "Quiz image url :${quiz.image}",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .weight(0.25f)
                                    .aspectRatio(16f / 9f)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Text(
                    text = quiz.lastUpdate?.format(DateTimeFormatter.ISO_DATE) ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
