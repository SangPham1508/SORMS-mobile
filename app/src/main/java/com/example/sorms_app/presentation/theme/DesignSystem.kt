package com.example.sorms_app.presentation.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design System constants for consistent spacing, typography, and layout
 * Matches web application design standards
 */
object DesignSystem {
    
    // Spacing Scale (8dp base unit)
    object Spacing {
        val xs = 4.dp      // 0.5x
        val sm = 8.dp     // 1x
        val md = 16.dp    // 2x
        val lg = 24.dp    // 3x
        val xl = 32.dp    // 4x
        val xxl = 48.dp   // 6x
        
        // Screen padding
        val screenHorizontal = 16.dp
        val screenVertical = 16.dp
        
        // Card padding
        val cardPadding = 16.dp
        val cardContentPadding = 16.dp
        
        // Item spacing in lists
        val listItemSpacing = 12.dp
        val listSectionSpacing = 24.dp
    }
    
    // Typography Scale
    object Typography {
        // Display
        val displayLarge = 32.sp
        val displayMedium = 28.sp
        val displaySmall = 24.sp
        
        // Headings
        val h1 = 24.sp
        val h2 = 20.sp
        val h3 = 18.sp
        val h4 = 16.sp
        
        // Body
        val bodyLarge = 16.sp
        val bodyMedium = 14.sp
        val bodySmall = 12.sp
        
        // Labels
        val labelLarge = 14.sp
        val labelMedium = 12.sp
        val labelSmall = 10.sp
    }
    
    // Component Sizes
    object Sizes {
        // Buttons
        val buttonHeight = 48.dp
        val buttonHeightSmall = 40.dp
        
        // Cards
        val cardCornerRadius = 16.dp
        val cardElevation = 2.dp
        
        // Icons
        val iconSmall = 16.dp
        val iconMedium = 24.dp
        val iconLarge = 32.dp
        
        // Avatar
        val avatarSmall = 32.dp
        val avatarMedium = 48.dp
        val avatarLarge = 64.dp
    }
    
    // Layout
    object Layout {
        val topAppBarHeight = 64.dp
        val bottomNavHeight = 64.dp
        val fabSize = 56.dp
    }
}



