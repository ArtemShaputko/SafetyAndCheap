package com.example.safetyandcheap.ui.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetyandcheap.service.dto.property.Apartment
import com.example.safetyandcheap.service.dto.property.House
import com.example.safetyandcheap.service.dto.property.Property
import com.example.safetyandcheap.service.dto.property.Room

@Composable
fun AuthChangeScreenTextButton(
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: String
) {
    Text(
        modifier = modifier.clickable(
            onClick = onClick,
            enabled = enabled
        ),
        color = Color(0xFF8F8F96),
        text = text,
        textAlign = textAlign,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun ContrastButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier.height(56.dp),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.tertiary
        ),
        enabled = enabled
    ) {
        content()
    }
}

@Composable
fun OutlinedContrastButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: Shape = RoundedCornerShape(10.dp),
    content: @Composable () -> Unit
) {
    OutlinedButton(
        modifier = modifier.height(56.dp),
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = Color.Transparent
        ),
        enabled = enabled,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary // Фиолетовый из темы
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            }
        ),
        contentPadding = contentPadding
    ) {
        content()
    }
}

@Composable
fun OutlinedContrastErrorButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        modifier = modifier.height(56.dp),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.error
        )
    ) {
        content()
    }
}

@Composable
fun BottomMenuButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    currentRoute: String?,
    routeToItem: String,
    content: @Composable () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (currentRoute == routeToItem)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        content()
    }
}

@Composable
fun LowContrastTextToggle(
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clickable(
                onClick = {
                    onSelectedChange(!isSelected)
                })
            .background(
                shape = RoundedCornerShape(10.dp),
                color = if (!isSelected) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.tertiary
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (!isSelected) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.background,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight(500),
            modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableTagsRow(
    tags: Set<String>,
    selectedTags: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            key(tag) {
                LowContrastTextToggle(
                    isSelected = selectedTags.contains(tag),
                    onSelectedChange = { isChecked ->
                        onSelectedChange(
                            if (isChecked) {
                                selectedTags + tag
                            } else {
                                selectedTags - tag
                            }
                        )
                    },
                    text = tag,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SingleSelectTagsRow(
    tags: Set<String>,
    selectedTag: String?,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            LowContrastTextToggle(
                isSelected = selectedTag == tag,
                onSelectedChange = { _ ->
                    onSelectedChange(tag)
                },
                text = tag,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsRow(
    tags: Set<String>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    textPadding: PaddingValues = PaddingValues(horizontal = 15.dp),
    tagShape: Shape = RoundedCornerShape(10.dp),
    tagColor: Color = MaterialTheme.colorScheme.secondary,
    rowHeight: Dp = 38.dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        tags.forEach { tag ->
            key(tag) {
                Box(
                    modifier = Modifier
                        .height(rowHeight)
                        .background(
                            shape = tagShape,
                            color = tagColor
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tag,
                        style = textStyle,
                        modifier = Modifier.padding(textPadding),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
fun  PropertyTagsRow(
    property: Property
) {
    val tags = when (property) {
        is House -> setOf(
            "${property.bedrooms} bedroom" + if(property.bedrooms>1) "s" else "",
            "${property.area.formatFractional()}m² total",
            "${property.siteArea.formatFractional()}m² site",
        )

        is Apartment -> setOf(
            "${property.rooms} room" + if(property.rooms>1) "s" else "",
            "${property.area.formatFractional()}m² total",
            "${property.livingArea.formatFractional()}m² living",
        )

        is Room -> setOf(
            "${property.roomsInOffer}/${property.roomsInApartment} rooms",
            "${property.area.formatFractional()}m² total",
            "${property.livingArea.formatFractional()}m² living",
        )
    }
    TagsRow(
        tags = tags,
        horizontalSpacing = 5.dp,
        verticalSpacing = 5.dp,
        textPadding = PaddingValues(horizontal = 5.dp),
        tagColor = Color(0xFF35364E),
        tagShape = RoundedCornerShape(size = 6.dp),
        modifier = Modifier.fillMaxWidth(),
        rowHeight = 17.dp,
        textStyle = TextStyle(
            fontSize = 11.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight(400),
            color = MaterialTheme.colorScheme.tertiary,
        )
    )
}