package com.example.safetyandcheap.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetyandcheap.ui.UiState

@Composable
fun NoElementsBox(
    uiState: UiState,
    mainText: String = "No matches",
    errorText: String = "Request error"
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            UiState.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )

            is UiState.NetworkError ->
                Text(
                    text = errorText,
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                )

            else ->
                Text(
                    text = mainText,
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                )

        }
    }
}