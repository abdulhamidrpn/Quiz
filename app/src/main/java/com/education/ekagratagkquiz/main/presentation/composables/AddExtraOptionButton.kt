package com.education.ekagratagkquiz.main.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.education.ekagratagkquiz.main.domain.models.OptionType

@Composable
fun AddExtraOptionButton(
    onAdd: (OptionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val selectedOptionType: MutableState<OptionType?> = remember {
            mutableStateOf(null)
        }
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
        Text(
            text = "Add Option",
            style = MaterialTheme.typography.bodyLarge,

            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )
        ChipGroup(
            selectedOptionType = selectedOptionType.value,
            onSelectedChanged = {
                onAdd(it)
            }
        )
    }
}

@Composable
@Preview
private fun AddExtraOptionPreview() {
    AddExtraOptionButton(onAdd = { })
}


@Preview(showBackground = true)
@Composable
fun Chip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {},
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) Color.LightGray else MaterialTheme.colorScheme.primary
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectionChanged(name)
                }
            )
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChipGroup(
    optionTypes: List<OptionType> = OptionType.entries,
    selectedOptionType: OptionType? = null,
    onSelectedChanged: (OptionType) -> Unit = {},
) {

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        LazyRow {
            items(optionTypes) {
                Chip(
                    name = it.name,
                    isSelected = selectedOptionType == it,
                    onSelectionChanged = { selectedOptionText ->
                        onSelectedChanged(OptionType.valueOf(selectedOptionText))
                    },
                )
            }
        }
    }
}