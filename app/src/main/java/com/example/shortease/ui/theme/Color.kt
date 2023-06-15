package com.example.shortease.ui.theme

import androidx.compose.ui.graphics.Color

data class ColorPalette(
    val ShortEaseRed: Color,
    val ShortEaseWhite: Color,
    val ShortEaseGrey: Color,
    val ShortEaseBlack: Color
)

val colorPalette = ColorPalette(
    ShortEaseRed = Color(0xFFEC405F),
    ShortEaseWhite = Color(0xFFFFFFFF),
    ShortEaseGrey = Color(0xEEFFFFFF),
    ShortEaseBlack = Color(0xF30C0C0C)
)