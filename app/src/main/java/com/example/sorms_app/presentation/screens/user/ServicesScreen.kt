package com.example.sorms_app.presentation.screens.user

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
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

    Column(
        modifier = modifier.fillMaxSize()
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

@Composable
private fun ServiceCard(
    service: Service,
    onAddToCart: () -> Unit,
    onServiceClick: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.cardContentPadding)
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
                    // Service Icon
                    Card(
                        modifier = Modifier.size(48.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = service.name,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = service.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Text(
                            text = service.code,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Service Description
            service.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Price and Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Giá dịch vụ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "${FormatUtils.formatCurrency(service.unitPrice)}/${service.unitName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onServiceClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Chi tiết")
                    }
                    
                    SormsButton(
                        onClick = onAddToCart,
                        text = "Thêm vào giỏ",
                        variant = ButtonVariant.Primary,
                        enabled = service.isActive,
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
        }
    }
}

