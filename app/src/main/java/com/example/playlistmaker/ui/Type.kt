package com.example.playlistmaker.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

val YSDisplay = FontFamily(
    Font(R.font.ys_display_regular),
    Font(R.font.ys_display_medium)
)

val AppTypography = Typography(
    // Аналог вашего стиля "Title"
    titleLarge = TextStyle(
        fontFamily = YSDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 22.sp
    ),
    // Аналог вашего стиля "Lines_settings"
    bodyLarge = TextStyle(
        fontFamily = YSDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp
    )
)