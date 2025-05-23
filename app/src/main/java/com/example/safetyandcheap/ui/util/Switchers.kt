package com.example.safetyandcheap.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LowContrastSwitcher(
    onOptionSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .height(38.dp)
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var selectedOption by remember { mutableStateOf(options.first()) }
        options.forEach { option ->
            Text(
                text = option,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        selectedOption = option
                        onOptionSelected(selectedOption)
                    }
                    .background(
                        color = if (selectedOption == option) MaterialTheme.colorScheme.tertiary.copy(
                            alpha = 0.898f
                        ) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .wrapContentHeight(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = if (selectedOption == option) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary.copy(
                    alpha = 0.898f
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight(500),
            )
        }
    }
}

@Composable
fun ContrastNumberRangeSwitcher(
    selectedMin: Int?,
    selectedMax: Int?,
    onOptionSelected: (Int?, Int?) -> Unit,
    borders: Pair<Int, Int>,
    modifier: Modifier = Modifier
) {

    val options = borders.first..borders.second
    val selectedRightBorder = selectedMax?:borders.second
    val currentRange: IntRange? = if(selectedMin == null)  null else selectedMin..selectedRightBorder

    Row(
        modifier = modifier
            .height(38.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                shape = RoundedCornerShape(10.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { num ->
            val isSelected = currentRange?.contains(num) == true
            val isPrevSelected = currentRange?.contains(num - 1) == true
            val isNextSelected = currentRange?.contains(num + 1) == true

            Text(
                text = if (num == borders.second) "$num+" else "$num",
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        var newMin: Int?
                        var newMax: Int?
                        when {
                            currentRange == null -> {
                                newMin = num
                                newMax = num
                            }

                            num < currentRange.first -> {
                                newMin = num
                                newMax = currentRange.last
                            }

                            num > currentRange.last -> {
                                newMin = currentRange.first
                                newMax = num
                            }

                            else -> {
                                newMin = null
                                newMax = null
                            }
                        }.also {
                                if (newMax == borders.second) newMax = null // "и более"
                                onOptionSelected(newMin, newMax)
                        }
                    }
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.tertiary
                        else Color.Transparent,
                        shape = RoundedCornerShape(
                            topStart = if (isPrevSelected) 0.dp else 10.dp,
                            bottomStart = if (isPrevSelected) 0.dp else 10.dp,
                            topEnd = if (isNextSelected) 0.dp else 10.dp,
                            bottomEnd = if (isNextSelected) 0.dp else 10.dp
                        )
                    )
                    .wrapContentHeight(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = if (isSelected) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ContrastNumberSwitcher(
    onOptionSelected: (Int) -> Unit,
    selectedOption: Int?,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val range = min..max
    Row(
        modifier = modifier
            .height(38.dp)
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        range.forEach { num ->
            Text(
                text = if (num == max) "$num+" else "$num",
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable { onOptionSelected(num) }
                    .background(
                        color = if (selectedOption == num) MaterialTheme.colorScheme.tertiary.copy(
                            alpha = 0.898f
                        ) else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .wrapContentHeight(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = if (selectedOption == num) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary.copy(
                    alpha = 0.898f
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight(500),
            )
        }
    }
}
