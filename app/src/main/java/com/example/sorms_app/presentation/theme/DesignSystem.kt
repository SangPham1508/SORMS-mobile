package com.example.sorms_app.presentation.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design System constants for consistent spacing, typography, and layout
 * Matches web application design standards
 */
object DesignSystem {
    
    // Spacing Scale (8dp base unit) - Tối ưu hóa cho white space
    object Spacing {
        val xs = 4.dp      // 0.5x
        val sm = 8.dp     // 1x
        val md = 16.dp    // 2x - Base spacing
        val lg = 24.dp    // 3x - Section spacing (tăng từ 16dp)
        val xl = 32.dp    // 4x - Large section spacing (tăng từ 24dp)
        val xxl = 48.dp   // 6x
        
        // Screen padding
        val screenHorizontal = 16.dp
        val screenVertical = 16.dp
        
        // Card padding - Tối ưu hóa
        val cardPadding = 20.dp      // Tăng từ 16dp để tạo white space
        val cardContentPadding = 12.dp  // Giảm từ 16dp để tối ưu nội dung
        
        // Item spacing in lists - Tăng để tạo white space
        val listItemSpacing = 16.dp     // Tăng từ 12dp
        val listSectionSpacing = 32.dp   // Tăng từ 24dp
        
        // Element spacing - Tăng spacing giữa các elements
        val elementSpacing = 12.dp   // Tăng từ 8dp
        val sectionSpacing = 24.dp   // Spacing giữa các sections
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



