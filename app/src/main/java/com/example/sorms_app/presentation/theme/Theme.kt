package com.example.sorms_app.presentation.theme

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

// Dark Color Scheme - Tối ưu cho dark mode với contrast tốt và màu sắc phù hợp
private val DarkColorScheme = darkColorScheme(
    primary = Blue300,  // Sáng hơn cho dark mode (#80B3FF)
    onPrimary = Color(0xFF00364C),  // Dark blue text trên primary
    primaryContainer = Blue800.copy(alpha = 0.4f),  // Dark blue container với alpha
    onPrimaryContainer = Blue100,  // Light blue text
    secondary = Gray400,  // Medium gray
    onSecondary = Color.White,
    secondaryContainer = Gray700,  // Darker gray container
    onSecondaryContainer = Gray100,  // Light gray text
    tertiary = Blue400,  // Medium blue
    onTertiary = Color.White,
    background = Color(0xFF121212),  // Material Dark background
    onBackground = Color(0xFFE0E0E0),  // Light gray text
    surface = Color(0xFF1E1E1E),  // Dark surface (slightly lighter than background)
    onSurface = Color(0xFFE0E0E0),  // Light gray text
    surfaceVariant = Color(0xFF2C2C2C),  // Lighter dark for cards
    onSurfaceVariant = Gray300,  // Medium gray text
    outline = Gray600,  // Border color
    error = Red500,
    onError = Color.White,
    errorContainer = Red800.copy(alpha = 0.3f),  // Dark red container
    onErrorContainer = Red100
)

// Light Color Scheme - Tối ưu cho light mode với màu sắc nhẹ nhàng và contrast tốt
private val LightColorScheme = lightColorScheme(
    primary = Blue600,  // #0085B8 - matches web hsl(200 98% 35%)
    onPrimary = Color.White,
    primaryContainer = Blue100,  // Light blue background for containers (#D6E4FF)
    onPrimaryContainer = Blue900,  // Dark blue text on primary container
    secondary = Gray600,  // Medium gray
    onSecondary = Color.White,
    secondaryContainer = Gray100,  // Light gray container
    onSecondaryContainer = Gray900,  // Dark gray text
    tertiary = Blue700,  // Darker blue
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),  // Slightly off-white background
    onBackground = Gray900,  // Dark gray text
    surface = Color.White,  // Pure white surface
    onSurface = Gray900,  // Dark gray text
    surfaceVariant = Gray50,  // Very light gray for card backgrounds (#F9FAFB)
    onSurfaceVariant = Gray700,  // Medium gray text
    outline = Gray200,  // Border color matching web (border-gray-200)
    error = Red500,
    onError = Color.White,
    errorContainer = Red100,  // Light red container
    onErrorContainer = Red800  // Dark red text
)

@Composable
fun SORMS_appTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use custom colors
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
            // Status bar color phù hợp với theme
            window.statusBarColor = if (darkTheme) {
                colorScheme.surface.toArgb()  // Dark surface cho dark mode
            } else {
                colorScheme.primary.toArgb()  // Primary color cho light mode
            }
            // Light status bar icons cho dark mode, dark icons cho light mode
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

