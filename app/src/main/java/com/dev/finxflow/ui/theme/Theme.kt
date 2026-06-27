package com.dev.finxflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ==========================================
// LIGHT COLOR SCHEME (Midnight Sapphire)
// ==========================================
private val LightColorScheme = lightColorScheme(
    primary               = PrimaryMain,
    onPrimary             = Color.White,
    primaryContainer      = Color(0xFFEEF2FF),
    onPrimaryContainer    = PrimaryMain,
    secondary             = PrimaryLight,
    onSecondary           = Color.White,
    secondaryContainer    = Color(0xFFE0E7FF),
    onSecondaryContainer  = PrimaryMain,
    tertiary              = AccentCyan,
    onTertiary            = Color.White,
    background            = BackgroundColor,
    onBackground          = TextPrimary,
    surface               = SurfaceColor,
    onSurface             = TextPrimary,
    surfaceVariant        = Color(0xFFF1F5F9),
    onSurfaceVariant      = TextSecondary,
    outline               = DividerColor,
    outlineVariant        = DividerColor,
    error                 = DangerRed,
    onError               = Color.White,
    errorContainer        = Color(0xFFFEF2F2),
    onErrorContainer      = DangerRed,
    inverseSurface        = TextPrimary,
    inverseOnSurface      = BackgroundColor,
    inversePrimary        = PrimaryLight,
    surfaceTint           = PrimaryMain,
    scrim                 = Color.Black.copy(alpha = 0.32f)
)

// ==========================================
// DARK COLOR SCHEME (Midnight Sapphire)
// ==========================================
private val DarkColorScheme = darkColorScheme(
    primary               = PrimaryLight,
    onPrimary             = PrimaryDark,
    primaryContainer      = Color(0xFF1E3A8A),
    onPrimaryContainer    = Color(0xFFE0E7FF),
    secondary             = Color(0xFF60A5FA),
    onSecondary           = PrimaryDark,
    secondaryContainer    = Color(0xFF1E40AF),
    onSecondaryContainer  = Color(0xFFE0E7FF),
    tertiary              = Color(0xFF22D3EE),
    onTertiary            = Color(0xFF0E7490),
    background            = Color(0xFF0F172A),
    onBackground          = Color(0xFFF1F5F9),
    surface               = Color(0xFF1E293B),
    onSurface             = Color(0xFFF1F5F9),
    surfaceVariant        = Color(0xFF334155),
    onSurfaceVariant      = TextTertiary,
    outline               = Color(0xFF475569),
    outlineVariant        = Color(0xFF475569),
    error                 = Color(0xFFFCA5A5),
    onError               = Color(0xFF7F1D1D),
    errorContainer        = Color(0xFF7F1D1D),
    onErrorContainer      = Color(0xFFFEE2E2),
    inverseSurface        = Color(0xFFF1F5F9),
    inverseOnSurface      = Color(0xFF0F172A),
    inversePrimary        = PrimaryMain,
    surfaceTint           = PrimaryLight,
    scrim                 = Color.Black.copy(alpha = 0.32f)
)

// ==========================================
// THEME COMPOSABLE
// ==========================================
@Composable
fun FinXFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FinXFlowTypography,
        content = content
    )
}