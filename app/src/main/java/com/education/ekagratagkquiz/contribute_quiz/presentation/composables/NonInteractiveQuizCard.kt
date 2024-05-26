package com.education.ekagratagkquiz.contribute_quiz.presentation.composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.domain.model.QuestionOption
import com.education.ekagratagkquiz.main.domain.models.QuestionModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonInteractiveQuizCard(
    questionModel: QuestionModel,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(.8f)
                ) {
                    if (questionModel.question.contains("IMAGE:")) {

                        Text(
                            text = buildAnnotatedString {
                                if (questionModel.isRequired)
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Black)) {
                                        append("*")
                                    }
                                append("Question")
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                        val image = questionModel.question.split("IMAGE:").lastOrNull()
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
                        val text = questionModel.question.split("TEXT:").lastOrNull()

                        Text(
                            text = buildAnnotatedString {
                                if (questionModel.isRequired)
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Black)) {
                                        append("*")
                                    }
                                append(text)
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                    }
                    questionModel.description?.let { desc ->
                        Text(
                            text = desc,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Thin)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete current question",
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
            Divider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = MaterialTheme.colorScheme.secondary
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(4.dp)
            ) {
                questionModel.options.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "${index + 1}.",
                            modifier = Modifier.weight(.1f),
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (item.option.contains("IMAGE:")) {
                            val imageOption = item.option.split("IMAGE:").lastOrNull()
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(imageOption)
                                    .crossfade(true).build(),
                                contentDescription = "Option image url :${imageOption}",
                                placeholder = painterResource(R.drawable.placeholder),
                                error = painterResource(R.drawable.placeholder_error),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,

                                modifier = Modifier
                                    .weight(.9f)
                                    .aspectRatio(16f / 9f)
                                    .clip(MaterialTheme.shapes.medium)
                            )


                        } else {
                            val textOption = item.option.split("TEXT:").lastOrNull() ?: item.option
                            Text(
                                text = textOption,
                                modifier = Modifier.weight(.9f),
                                letterSpacing = 0.75.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }


                    }
                }
            }
        }
    }
}

private class NonInteractiveQuizCardParams : PreviewParameterProvider<QuestionModel> {

    override val values = sequenceOf(
        QuestionModel(
            uid = "o44948DaNt3EX5oLX2WA",
            question = "What is the the formula of momentum.",
            description = "You may refer the answer to physics book page something",
            isRequired = true,
            options = listOf(
                QuestionOption(option = "p=mv", isSelected = true),
                QuestionOption(option = "p=m/v"),
                QuestionOption(option = "p=m2v")
            )
        )
    )
}

@Composable
@Preview
private fun NonInteractiveQuizCardPreview(
    @PreviewParameter(NonInteractiveQuizCardParams::class) questionModel: QuestionModel
) {
    NonInteractiveQuizCard(
        questionModel = questionModel,
        onDelete = {}
    )
}