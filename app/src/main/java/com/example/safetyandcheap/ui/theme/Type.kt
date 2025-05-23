package com.example.safetyandcheap.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight(600),
    ),
    titleSmall = TextStyle(
        fontSize = 18.sp,
        lineHeight = 19.8.sp,
        fontWeight = FontWeight(400)
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontSize = 40.sp,
        lineHeight = 52.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Normal,
        fontSize = 16.sp,
        lineHeight = 20.8.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontSize = 15.sp,
        lineHeight = 19.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontSize = 12.sp,
        lineHeight = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight(400),
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Normal,
        fontSize = 12.sp,
        lineHeight = 10.8.sp
    )
)