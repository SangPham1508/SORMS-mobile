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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.FormatUtils
import com.example.sorms_app.presentation.viewmodel.ServiceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onServiceSelected: (Service) -> Unit,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Multi-step service order modal
    MultiStepServiceOrderModal(
        service = uiState.selectedServiceForOrder,
        isOpen = uiState.isServiceOrderModalOpen,
        onDismiss = viewModel::closeServiceOrderModal,
        onOrderSuccess = {
            viewModel.closeServiceOrderModal()
            onNavigateToOrders()
        },
        bookings = uiState.userBookings,
        staffOptions = uiState.staffOptions,
        isLoadingBookings = false,
        isLoadingStaff = uiState.isLoadingStaff,
        createOrderCart = { bookingId, note ->
            viewModel.createOrderCart(bookingId, note)
        },
        addOrderItem = { orderId, serviceId, quantity ->
            viewModel.addOrderItem(orderId, serviceId, quantity)
        },
        createServiceOrder = { bookingId, orderId, serviceId, quantity, assignedStaffId, requestedBy, serviceTime, note ->
            // requestedBy is determined in ViewModel, ignore param
            viewModel.createServiceOrder(
                bookingId = bookingId,
                orderId = orderId,
                serviceId = serviceId,
                quantity = quantity,
                assignedStaffId = assignedStaffId,
                serviceTime = serviceTime,
                note = note
            )
        }
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SormsTopAppBar(
                title = "Dịch vụ",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onViewCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Giỏ hàng"
                        )
                    }
                }
            )

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
                            items(uiState.services) { service ->
                                ServiceCard(
                                    service = service,
                                    onAddToCart = { viewModel.openServiceOrderModal(service) },
                                    onServiceClick = { onServiceSelected(service) }
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
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignSystem.Spacing.cardPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = service.name,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.sm))

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

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SormsButton(
                            onClick = onServiceClick,
                            text = "Chi tiết",
                            variant = ButtonVariant.Secondary,
                            isOutlined = true,
                            modifier = Modifier.weight(1f)
                        )

                        SormsButton(
                            onClick = onAddToCart,
                            text = "Đặt",
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
        ServicesScreen(
            onNavigateBack = {},
            onNavigateToOrders = {},
            onServiceSelected = {},
            onViewCart = {}
        )
    }
}
