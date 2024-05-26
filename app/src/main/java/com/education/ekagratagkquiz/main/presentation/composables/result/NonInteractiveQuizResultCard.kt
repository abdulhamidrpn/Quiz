package com.education.ekagratagkquiz.main.presentation.composables.result

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import com.education.ekagratagkquiz.main.util.FinalQuizOptionState
import com.education.ekagratagkquiz.ui.theme.success
import com.education.ekagratagkquiz.ui.theme.successContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonInterActiveQuizResultCard(
    optionState: List<FinalQuizOptionState>,
    quizIndex: Int,
    quiz: QuestionModel,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Log.d("TAG", "NonInterActiveQuizResultCard: ${optionState[quizIndex].isCorrect}")
    OutlinedCard(
        modifier = modifier
            .padding(vertical = 4.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        border = BorderStroke(
            2.dp,
            color = if (optionState[quizIndex].isCorrect) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(2.dp)
            ) {
                // TODO: Add marks for this question at right
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                if (optionState[quizIndex].isCorrect) {
                                    append("+01")
                                } else if (optionState[quizIndex].option.isNullOrEmpty()) {
                                    append("00")
                                } else {
                                    append("-0.25")
                                }
                            }
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                if (quiz.question.contains("IMAGE:")) {

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Question ${quizIndex + 1} : ")
                            }
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    val image = quiz.question.split("IMAGE:").lastOrNull()
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(image).build(),
                        contentDescription = "Question image url :${image}",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(MaterialTheme.shapes.medium)
                    )
                } else {

                    val text = quiz.question.split("TEXT:").lastOrNull()
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Question ${quizIndex + 1} : ")
                            }
                            append(text)
                        }, style = MaterialTheme.typography.titleMedium
                    )

                }

                quiz.description?.let { desc ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append("Description: ")
                            }
                            append(desc)
                        },
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (quiz.isRequired) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize
                            )
                        ) {
                            append(" * ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                        ) {
                            append("Required")
                        }
                    },
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            Divider(
                modifier = Modifier.padding(vertical = 2.dp)
            )
            quiz.options.forEachIndexed { index, opt ->

                if (opt.option.contains("INPUT:")) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {

                        val textOption = opt.option.split("INPUT:").lastOrNull() ?: opt.option

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Answer : ")
                                }
                                append(textOption)
                            }, style = MaterialTheme.typography.titleMedium
                        )
                        val textSubmitted =
                            optionState[quizIndex].option?.firstOrNull()?.option ?: ""

                        Text(
                            text = buildAnnotatedString {

                                withStyle(
                                    style = SpanStyle(
                                        color = if (textOption.lowercase() == textSubmitted.lowercase()) {
                                            Color.Green
                                        } else {
                                            Color.Red
                                        },
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("Submitted : ")
                                }

                                append(textSubmitted)

                            }, style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .background(
                                color = if (optionState[quizIndex].option?.contains(opt) == true) {
                                    if (opt.isSelected)
                                        MaterialTheme.colorScheme.successContainer
                                    else
                                        MaterialTheme.colorScheme.errorContainer

                                } else
                                    Color.Transparent,
                                shape = MaterialTheme.shapes.medium
                            )
                            .border(
                                1.2f.dp,
                                color = if (optionState[quizIndex].option?.contains(opt) == true) {
                                    if (opt.isSelected)
                                        MaterialTheme.colorScheme.success
                                    else
                                        MaterialTheme.colorScheme.error
                                } else
                                    Color.Transparent,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {


                        Row(
                            modifier = Modifier.weight(.9f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            if (opt.option.contains("IMAGE:")) {
                                val imageOption = opt.option.split("IMAGE:").lastOrNull()
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(imageOption)
                                        .crossfade(true).build(),
                                    contentDescription = "Option image url :${imageOption}",
                                    placeholder = painterResource(R.drawable.placeholder),
                                    error = painterResource(R.drawable.placeholder_error),
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center,

                                    modifier = Modifier
                                        .aspectRatio(16f / 9f)
                                        .clip(MaterialTheme.shapes.medium)
                                )
                            } else {
                                val textOption =
                                    opt.option.split("TEXT:").lastOrNull() ?: opt.option
                                Text(
                                    text = textOption,
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }


                            Spacer(modifier = Modifier.width(12.dp))
                        }



                        if (opt.isSelected) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircleOutline,
                                tint = Color.Green,
                                contentDescription = "Correct response"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                tint = Color.Red,
                                contentDescription = "Wrong response"
                            )
                        }
                    }
                }
            }



            quiz.questionExplanation?.let { explanation ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        expandedState = !expandedState
                    }
                ) {
                    Text(
                        modifier = Modifier
                            .weight(6f),
                        text = if (expandedState) "Click to hide the explanation" else "Click to see the explanation",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .alpha(0.8f)
                            .rotate(rotationState),
                        onClick = {
                            expandedState = !expandedState
                        }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Drop-Down Arrow"
                        )
                    }
                }

                if (expandedState) {
                    Divider(
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Text(
                        text = explanation,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

