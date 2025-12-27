package com.example.sorms_app.presentation.screens.staff.oders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.model.ServiceOrderStatus
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.FormatUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.StaffOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffOrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderSelected: (ServiceOrder) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StaffOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    // Load data when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    val filteredOrders = remember(uiState.orders, selectedFilter) {
        when (selectedFilter) {
            "Chờ xác nhận" -> uiState.orders.filter { it.status == ServiceOrderStatus.PENDING }
            "Đã xác nhận" -> uiState.orders.filter { it.status == ServiceOrderStatus.CONFIRMED }
            "Đang thực hiện" -> uiState.orders.filter { it.status == ServiceOrderStatus.IN_PROGRESS }
            "Hoàn thành" -> uiState.orders.filter { it.status == ServiceOrderStatus.COMPLETED }
            else -> uiState.orders
        }
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
                title = "Quản lý đơn hàng",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refresh()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới"
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

                uiState.orders.isEmpty() -> {
                    SormsEmptyState(
                        title = "Không có đơn hàng",
                        subtitle = "Hiện tại không có đơn hàng nào cần xử lý"
                    )
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Filter Section
                            item {
                                FilterSection(
                                    selectedFilter = selectedFilter,
                                    onFilterChanged = { filter -> selectedFilter = filter }
                                )
                            }

                            // Order Statistics
                            item {
                                OrderStatisticsCard(
                                    totalOrders = uiState.orders.size,
                                    pendingOrders = uiState.orders.count { it.status == ServiceOrderStatus.PENDING },
                                    confirmedOrders = uiState.orders.count { it.status == ServiceOrderStatus.CONFIRMED },
                                    completedOrders = uiState.orders.count { it.status == ServiceOrderStatus.COMPLETED }
                                )
                            }

                            // Orders List
                            items(filteredOrders) { order ->
                                StaffOrderCard(
                                    order = order,
                                    onOrderClick = { onOrderSelected(order) },
                                    onConfirmOrder = { viewModel.confirmOrder(order.id) },
                                    onStartOrder = { viewModel.startOrder(order.id) },
                                    onCompleteOrder = { viewModel.completeOrder(order.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    selectedFilter: String,
    onFilterChanged: (String) -> Unit
) {
    val filters = listOf("Tất cả", "Chờ xác nhận", "Đã xác nhận", "Đang thực hiện", "Hoàn thành")

    SormsCard {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            Text(
                text = "Lọc đơn hàng",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChanged(filter) },
                        label = { Text(filter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderStatisticsCard(
    totalOrders: Int,
    pendingOrders: Int,
    confirmedOrders: Int,
    completedOrders: Int
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            Text(
                text = "Thống kê đơn hàng",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatisticItem(
                    icon = Icons.Default.Receipt,
                    label = "Tổng",
                    value = totalOrders.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.Schedule,
                    label = "Chờ",
                    value = pendingOrders.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.PlayArrow,
                    label = "Đang làm",
                    value = confirmedOrders.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Xong",
                    value = completedOrders.toString(),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = color
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StaffOrderCard(
    order: ServiceOrder,
    onOrderClick: () -> Unit,
    onConfirmOrder: () -> Unit,
    onStartOrder: () -> Unit,
    onCompleteOrder: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Đơn hàng #${order.code}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = DateUtils.formatDate(order.createdDate),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                SormsBadge(
                    text = StatusUtils.getServiceOrderStatusText(order.status.name),
                    tone = StatusUtils.getServiceOrderStatusBadgeTone(order.status.name)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrderDetailItem(
                    icon = Icons.Default.ShoppingBag,
                    label = "Dịch vụ",
                    value = "${order.items.size} mục",
                    modifier = Modifier.weight(1f)
                )

                OrderDetailItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Tổng tiền",
                    value = FormatUtils.formatCurrency(order.totalAmount),
                    modifier = Modifier.weight(1f)
                )

                OrderDetailItem(
                    icon = Icons.Default.Business,
                    label = "Phòng",
                    value = "Phòng ${order.bookingId ?: "N/A"}",
                    modifier = Modifier.weight(1f)
                )
            }

            // Services Summary
            if (order.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Dịch vụ: ${
                        order.items.take(2).joinToString(", ") { it.serviceName }
                    }${if (order.items.size > 2) "..." else ""}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Notes
            order.note?.let { note ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ghi chú: $note",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOrderClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Chi tiết")
                }

                when (order.status) {
                    ServiceOrderStatus.PENDING -> {
                        SormsButton(
                            onClick = onConfirmOrder,
                            text = "Xác nhận",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    ServiceOrderStatus.CONFIRMED -> {
                        SormsButton(
                            onClick = onStartOrder,
                            text = "Bắt đầu",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    ServiceOrderStatus.IN_PROGRESS -> {
                        SormsButton(
                            onClick = onCompleteOrder,
                            text = "Hoàn thành",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    else -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, name = "Staff Orders Screen")
@Composable
private fun StaffOrdersScreenPreview() {
    SORMS_appTheme {
        StaffOrdersScreen(
            onNavigateBack = {},
            onOrderSelected = {}
        )
    }
}


