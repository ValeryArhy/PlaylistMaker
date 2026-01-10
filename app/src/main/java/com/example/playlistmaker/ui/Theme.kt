package com.example.playlistmaker.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = YP_Blue,
    secondary = YP_Light_Blue,
    background = YP_Black,
    surface = YP_Black,
    onPrimary = YP_White,
    onBackground = YP_White,
    onSurface = YP_White,
    error = YP_White,
    onSurfaceVariant = YP_White,
    inverseOnSurface = YP_Black,
    onPrimaryContainer = YP_White,
    primaryContainer = YP_Black

)

private val LightColorScheme = lightColorScheme(
    primary = YP_Blue,
    secondary = YP_Light_Blue,
    background = YP_White,
    surface = YP_White,
    onPrimary = YP_White,
    onBackground = YP_Black,
    onSurface = YP_Black,
    error = YP_Red,
    onSurfaceVariant = YP_Gray,
    inverseOnSurface = YP_White,
    onPrimaryContainer = Light_Gray,
    primaryContainer = YP_Gray


)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}