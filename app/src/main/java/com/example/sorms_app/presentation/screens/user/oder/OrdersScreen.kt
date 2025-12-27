package com.example.sorms_app.presentation.screens.user.oder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.model.ServiceOrderStatus
import com.example.sorms_app.presentation.components.ButtonVariant
import com.example.sorms_app.presentation.components.SormsBadge
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsTopAppBar
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.FormatUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderSelected: (ServiceOrder) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    val filteredOrders = remember(uiState.orders, selectedFilter) {
        when (selectedFilter) {
            "Chờ xử lý" -> uiState.orders.filter { it.status == ServiceOrderStatus.PENDING }
            "Đang thực hiện" -> uiState.orders.filter {
                it.status == ServiceOrderStatus.CONFIRMED || it.status == ServiceOrderStatus.IN_PROGRESS
            }
            "Hoàn thành" -> uiState.orders.filter { it.status == ServiceOrderStatus.COMPLETED }
            "Đã hủy" -> uiState.orders.filter { it.status == ServiceOrderStatus.CANCELLED }
            else -> uiState.orders
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SormsTopAppBar(
                title = "Đơn hàng của tôi",
                onNavigateBack = onNavigateBack
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
                        title = "Chưa có đơn hàng",
                        subtitle = "Bạn chưa có đơn hàng nào. Hãy đặt dịch vụ để bắt đầu!"
                    )
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                FilterSection(
                                    selectedFilter = selectedFilter,
                                    onFilterChanged = { filter -> selectedFilter = filter }
                                )
                            }

                            items(items = filteredOrders) { order ->
                                OrderCard(
                                    order = order,
                                    onOrderClick = { onOrderSelected(order) },
                                    onCancelOrder = { viewModel.cancelOrder(order.id) },
                                    onPayOrder = { viewModel.payOrder(order.id) }
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
    val filters = listOf("Tất cả", "Chờ xử lý", "Đang thực hiện", "Hoàn thành", "Đã hủy")

    SormsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Lọc đơn hàng",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
private fun OrderCard(
    order: ServiceOrder,
    onOrderClick: () -> Unit,
    onCancelOrder: () -> Unit,
    onPayOrder: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrderDetailItem(
                    icon = Icons.Default.ShoppingBag,
                    label = "Số lượng",
                    value = "${order.items.size} dịch vụ",
                    modifier = Modifier.weight(1f)
                )

                OrderDetailItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Tổng tiền",
                    value = FormatUtils.formatCurrency(order.totalAmount),
                    modifier = Modifier.weight(1f)
                )

                OrderDetailItem(
                    icon = Icons.Default.Person,
                    label = "Nhân viên",
                    value = "Chưa phân công",
                    modifier = Modifier.weight(1f)
                )
            }

            if (order.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Dịch vụ: ${order.items.take(2).joinToString(", ") { it.serviceName }}${if (order.items.size > 2) "..." else ""}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SormsButton(
                    onClick = onOrderClick,
                    text = "Chi tiết",
                    variant = ButtonVariant.Secondary,
                    isOutlined = true,
                    modifier = Modifier.weight(1f)
                )

                when (order.status) {
                    ServiceOrderStatus.PENDING -> {
                        SormsButton(
                            onClick = onCancelOrder,
                            text = "Hủy đơn",
                            variant = ButtonVariant.Danger,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    ServiceOrderStatus.CONFIRMED -> {
                        SormsButton(
                            onClick = onPayOrder,
                            text = "Thanh toán",
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
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, name = "Orders Screen")
@Composable
private fun OrdersScreenPreview() {
    SORMS_appTheme {
        OrdersScreen(
            onNavigateBack = {},
            onOrderSelected = {}
        )
    }
}
