package com.education.ekagratagkquiz.main.presentation.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.main.domain.models.QuestionModel
import com.education.ekagratagkquiz.main.util.FinalQuizOptionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterActiveQuizCard(
    optionState: List<FinalQuizOptionState>,
    quizIndex: Int,
    quiz: QuestionModel,
    modifier: Modifier = Modifier,
    onPick: (QuestionOption) -> Unit,
    onUnpick: () -> Unit,
    onInputAnswer: (String) -> Unit,
    context: Context = LocalContext.current
) {
    OutlinedCard(
        modifier = modifier.padding(vertical = 4.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(2.dp)
            ) {
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
            quiz.shuffledOptions.forEach { opt ->

                if (opt.option.contains("INPUT:")) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = optionState[quizIndex].option?.firstOrNull()?.option ?: "",
                            onValueChange = {onInputAnswer(it)},
                            maxLines = 2,
                            placeholder = { Text(text = "Input your answer.") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                            .apply {
                                if (optionState[quizIndex].option?.contains(opt) == true)
                                    background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                        .border(
                                            1.25.dp,
                                            MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.medium
                                        )
                            }
                            .clickable(onClick = { onPick(opt) }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Log.d(
                            "TAG", "onOptionEvent: OptionPicked" +
                                    "\noptionState ${optionState[quizIndex].option}" +
                                    "\nopt  pick   $opt" +
                                    "\n" +
                                    ""
                        )

                        RadioButton(
                            selected = optionState[quizIndex].option?.contains(opt) == true,
                            onClick = { onPick(opt) },
                            modifier = Modifier.padding(0.dp)
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
                            val textOption = opt.option.split("TEXT:").lastOrNull() ?: opt.option
                            Text(
                                text = textOption,
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }


                    }
                }
            }
            Divider(color = MaterialTheme.colorScheme.secondary)
            TextButton(
                onClick = onUnpick, modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Outlined.RemoveCircleOutline,
                    contentDescription = "Remove response"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Clear Response", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

