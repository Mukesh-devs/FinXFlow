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
// COLOR PALETTE (Design Spec)
// ==========================================
//val IndigoPrimary        = Color(0xFF4F46E5)
//val IndigoGradientStart  = Color(0xFF5B5FF7)
//val IndigoGradientEnd    = Color(0xFF4F46E5)
//val BackgroundLight      = Color(0xFFF5F7FF)
//val SurfaceLight         = Color(0xFFFFFFFF)
//val TextPrimary          = Color(0xFF1E293B)
//val TextSecondary        = Color(0xFF64748B)
//val DividerColor         = Color(0xFFE2E8F0)
//val ProgressTrack        = Color(0xFFE2E8F0)
//val SuccessGreen         = Color(0xFF10B981)
//val ErrorRed             = Color(0xFFEF4444)
//
//// ==========================================
//// LIGHT COLOR SCHEME
//// ==========================================
//private val LightColorScheme = lightColorScheme(
//    primary               = IndigoPrimary,
//    onPrimary             = Color.White,
//    primaryContainer      = Color(0xFFEEF2FF),
//    onPrimaryContainer    = IndigoPrimary,
//    secondary             = IndigoGradientStart,
//    onSecondary           = Color.White,
//    secondaryContainer    = Color(0xFFE0E7FF),
//    onSecondaryContainer  = IndigoPrimary,
//    tertiary              = Color(0xFF8B5CF6),
//    onTertiary            = Color.White,
//    background            = BackgroundLight,
//    onBackground          = TextPrimary,
//    surface               = SurfaceLight,
//    onSurface             = TextPrimary,
//    surfaceVariant        = Color(0xFFF1F5F9),
//    onSurfaceVariant      = TextSecondary,
//    outline               = DividerColor,
//    outlineVariant        = DividerColor,
//    error                 = ErrorRed,
//    onError               = Color.White,
//    errorContainer        = Color(0xFFFEF2F2),
//    onErrorContainer      = ErrorRed,
//    inverseSurface        = TextPrimary,
//    inverseOnSurface      = BackgroundLight,
//    inversePrimary        = Color(0xFFA5B4FC),
//    surfaceTint           = IndigoPrimary,
//    scrim                 = Color.Black.copy(alpha = 0.32f)
//)
//
//// ==========================================
//// DARK COLOR SCHEME (Optional)
//// ==========================================
//private val DarkColorScheme = darkColorScheme(
//    primary               = Color(0xFFA5B4FC),
//    onPrimary             = Color(0xFF312E81),
//    primaryContainer      = Color(0xFF4338CA),
//    onPrimaryContainer    = Color(0xFFE0E7FF),
//    secondary             = Color(0xFF818CF8),
//    onSecondary           = Color(0xFF312E81),
//    secondaryContainer    = Color(0xFF3730A3),
//    onSecondaryContainer  = Color(0xFFE0E7FF),
//    tertiary              = Color(0xFFC4B5FD),
//    onTertiary            = Color(0xFF4C1D95),
//    background            = Color(0xFF0F172A),
//    onBackground          = Color(0xFFF1F5F9),
//    surface               = Color(0xFF1E293B),
//    onSurface             = Color(0xFFF1F5F9),
//    surfaceVariant        = Color(0xFF334155),
//    onSurfaceVariant      = Color(0xFF94A3B8),
//    outline               = Color(0xFF475569),
//    outlineVariant        = Color(0xFF475569),
//    error                 = Color(0xFFFCA5A5),
//    onError               = Color(0xFF7F1D1D),
//    errorContainer        = Color(0xFF7F1D1D),
//    onErrorContainer      = Color(0xFFFEE2E2),
//    inverseSurface        = Color(0xFFF1F5F9),
//    inverseOnSurface      = Color(0xFF0F172A),
//    inversePrimary        = IndigoPrimary,
//    surfaceTint           = Color(0xFFA5B4FC),
//    scrim                 = Color.Black.copy(alpha = 0.32f)
//)
//
//// ==========================================
//// THEME COMPOSABLE
//// ==========================================
//@Composable
//fun FinXFlowTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    dynamicColor: Boolean = false, // Disabled to keep brand colors consistent
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.background.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
//        }
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = FinXFlowTypography,
//        content = content
//    )
//}