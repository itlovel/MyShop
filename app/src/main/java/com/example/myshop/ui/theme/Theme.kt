package com.example.myshop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TokoColorScheme = lightColorScheme(
    primary          = NavyPrimary,
    onPrimary        = CardWhite,
    primaryContainer = ProductIconBg,
    onPrimaryContainer = ProductIconTint,

    secondary        = YellowAccent,
    onSecondary      = NavyPrimary,

    background       = BackgroundLight,
    onBackground     = TextPrimary,

    surface          = CardWhite,
    onSurface        = TextPrimary,

    error            = ErrorRed,
    onError          = CardWhite,

    outline          = BorderColor,
    outlineVariant   = DashedBorder,
)

@Composable
fun MyShopTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TokoColorScheme,
        typography  = Typography,
        content     = content
    )
}