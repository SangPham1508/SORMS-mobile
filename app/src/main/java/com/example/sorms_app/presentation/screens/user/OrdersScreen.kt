package com.example.sorms_app.presentation.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.model.ServiceOrderStatus
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderSelected: (ServiceOrder) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Đơn hàng của tôi",
                    fontWeight = FontWeight.SemiBold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
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
            
            uiState.orders.isEmpty() -> {
                SormsEmptyState(
                    title = "Chưa có đơn hàng",
                    subtitle = "Bạn chưa có đơn hàng nào. Hãy đặt dịch vụ để bắt đầu!"
                )
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Filter Section
                    item {
                        FilterSection(
                            selectedFilter = selectedFilter,
                            onFilterChanged = { selectedFilter = it }
                        )
                    }
                    
                    // Orders List
                    items(filteredOrders) { order ->
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

@Composable
private fun FilterSection(
    selectedFilter: String,
    onFilterChanged: (String) -> Unit
) {
    val filters = listOf("Tất cả", "Chờ xử lý", "Đang thực hiện", "Hoàn thành", "Đã hủy")
    
    SormsCard {
        Column(
            modifier = Modifier.padding(16.dp)
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
                        text = formatDate(order.createdDate ?: ""),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                SormsBadge(
                    text = getOrderStatusText(order.status),
                    tone = getOrderStatusBadgeTone(order.status)
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
                    label = "Số lượng",
                    value = "${order.items.size} dịch vụ",
                    modifier = Modifier.weight(1f)
                )
                
                OrderDetailItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Tổng tiền",
                    value = formatCurrency(order.totalAmount),
                    modifier = Modifier.weight(1f)
                )
                
                OrderDetailItem(
                    icon = Icons.Default.Person,
                    label = "Nhân viên",
                    value = "Chưa phân công", // Mock data
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Services Summary
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
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOrderClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Chi tiết")
                }
                
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

// Helper functions
private fun getOrderStatusText(status: ServiceOrderStatus): String {
    return status.displayName
}

private fun getOrderStatusBadgeTone(status: ServiceOrderStatus): BadgeTone {
    return when (status) {
        ServiceOrderStatus.COMPLETED -> BadgeTone.Success
        ServiceOrderStatus.PENDING -> BadgeTone.Warning
        ServiceOrderStatus.CONFIRMED, ServiceOrderStatus.IN_PROGRESS -> BadgeTone.Default
        ServiceOrderStatus.CANCELLED -> BadgeTone.Error
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount)
}

private fun formatDate(dateString: String): String {
    if (dateString.isEmpty()) return ""
    return try {
        // Assuming ISO format, adjust as needed
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}