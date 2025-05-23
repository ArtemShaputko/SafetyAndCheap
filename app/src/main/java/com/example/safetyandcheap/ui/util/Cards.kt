package com.example.safetyandcheap.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AddPropertyCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(134.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
    ) {
        content()
    }
}