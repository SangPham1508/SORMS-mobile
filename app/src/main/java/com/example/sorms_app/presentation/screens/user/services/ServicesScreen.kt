package com.example.sorms_app.presentation.screens.user.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.FormatUtils
import com.example.sorms_app.presentation.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onNavigateBack: () -> Unit,
    onServiceSelected: (Service) -> Unit,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load data when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadServices()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        SormsTopAppBar(
            title = "Dịch vụ",
            onNavigateBack = onNavigateBack,
            actions = {
                // Cart Icon with badge
                IconButton(onClick = onViewCart) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Giỏ hàng"
                    )
                }
            }
        )

        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Có lỗi xảy ra",
                    subtitle = uiState.errorMessage!!
                )
            }
            
            uiState.services.isEmpty() -> {
                SormsEmptyState(
                    title = "Không có dịch vụ",
                    subtitle = "Hiện tại không có dịch vụ nào khả dụng"
                )
            }
            
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(DesignSystem.Spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.listItemSpacing)
                ) {
                    // Services List
                    items(uiState.services) { service ->
                        ServiceCard(
                            service = service,
                            onAddToCart = { /* TODO: Add to cart functionality */ },
                            onServiceClick = { onServiceSelected(service) }
                        )
                    }
                    
                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
        
        // Cart Summary FAB - Hide for now since cart is not implemented
        // if (uiState.cartItemsCount > 0) { ... }
        }
    }
}

@Composable
private fun ServiceCard(
    service: Service,
    onAddToCart: () -> Unit,
    onServiceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
        ) {
            // Service Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        // Service Icon with gradient background
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        )
                        ),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = service.name,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                    }
                    
                        Spacer(modifier = Modifier.width(14.dp))
                    
                    Column {
                        Text(
                            text = service.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                            
                            Spacer(modifier = Modifier.height(2.dp))
                        
                        Text(
                            text = service.code,
                            fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                if (service.isActive) {
                    SormsBadge(
                        text = "Khả dụng",
                        tone = BadgeTone.Success
                    )
                } else {
                    SormsBadge(
                        text = "Tạm ngừng",
                        tone = BadgeTone.Error
                    )
                }
            }
            
                Spacer(modifier = Modifier.height(14.dp))
            
            // Service Description
            service.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                )
                
                    Spacer(modifier = Modifier.height(16.dp))
            }
            
                // Price and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Giá dịch vụ",
                        fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                    )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${FormatUtils.formatCurrency(service.unitPrice)}/${service.unitName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                        SormsButton(
                        onClick = onServiceClick,
                            text = "Chi tiết",
                            variant = ButtonVariant.Secondary,
                            isOutlined = true,
                            modifier = Modifier.weight(1f)
                        )
                    
                    SormsButton(
                        onClick = onAddToCart,
                            text = "Thêm",
                        variant = ButtonVariant.Primary,
                        enabled = service.isActive,
                            modifier = Modifier.width(130.dp)
                    )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Services Screen", device = "spec:width=411dp,height=891dp")
@Composable
private fun ServicesScreenPreview() {
    SORMS_appTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top App Bar
                SormsTopAppBar(
                    title = "Dịch vụ",
            onNavigateBack = {},
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Giỏ hàng"
                            )
                        }
                    }
                )
                
                // Mock Services List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(DesignSystem.Spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.listItemSpacing)
                ) {
                    items(getMockServices()) { service ->
                        ServiceCard(
                            service = service,
                            onAddToCart = {},
                            onServiceClick = {}
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

private fun getMockServices(): List<Service> {
    return listOf(
        Service(
            id = "1",
            name = "Dọn dẹp phòng",
            code = "SV-001",
            description = "Dịch vụ dọn dẹp phòng hàng ngày với đội ngũ nhân viên chuyên nghiệp",
            unitPrice = 50000.0,
            unitName = "lần",
            isActive = true,
            icon = Icons.Default.CleaningServices
        ),
        Service(
            id = "2",
            name = "Giặt ủi",
            code = "SV-002",
            description = "Dịch vụ giặt ủi quần áo nhanh chóng và chất lượng",
            unitPrice = 30000.0,
            unitName = "kg",
            isActive = true,
            icon = Icons.Default.DryCleaning
        ),
        Service(
            id = "3",
            name = "Đưa đón sân bay",
            code = "SV-003",
            description = "Dịch vụ đưa đón sân bay với xe đời mới, tài xế chuyên nghiệp",
            unitPrice = 200000.0,
            unitName = "chuyến",
            isActive = true,
            icon = Icons.Default.DirectionsCar
        ),
        Service(
            id = "4",
            name = "Massage",
            code = "SV-004",
            description = "Dịch vụ massage thư giãn tại phòng",
            unitPrice = 150000.0,
            unitName = "giờ",
            isActive = false,
            icon = Icons.Default.HealthAndSafety
        )
    )
}

