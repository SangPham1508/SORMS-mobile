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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.PaymentMethod
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.model.ServiceOrderStatus
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    
    val order = uiState.orders.find { it.id == orderId }

    LaunchedEffect(orderId) {
        if (order == null) {
            viewModel.loadOrders()
        }
    }

    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) {
            onPaymentSuccess()
            viewModel.clearPaymentState()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Chi tiết đơn hàng",
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

        if (order == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    SormsEmptyState(
                        title = "Không tìm thấy đơn hàng",
                        subtitle = "Đơn hàng không tồn tại hoặc đã bị xóa"
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Order Header
                item {
                    SormsCard {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Text(
                                        text = "Tạo lúc: ${formatDate(order.createdDate ?: "")}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    
                                    order.lastModifiedDate?.let { lastModified ->
                                        Text(
                                            text = "Cập nhật: ${formatDate(lastModified)}",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                
                                SormsBadge(
                                    text = getOrderStatusText(order.status),
                                    tone = getOrderStatusBadgeTone(order.status)
                                )
                            }
                            
                            order.note?.let { note ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Ghi chú: $note",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Order Items
                item {
                    SormsCard {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Dịch vụ đã đặt",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                items(order.items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.serviceName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Text(
                                    text = "Số lượng: ${item.quantity}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                
                                Text(
                                    text = "Đơn giá: ${formatCurrency(item.unitPrice)}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            Text(
                                text = formatCurrency(item.lineTotal),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Price Summary
                item {
                    SormsCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Tóm tắt thanh toán",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tạm tính")
                                Text(formatCurrency(order.subtotalAmount))
                            }
                            
                            if (order.discountAmount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Giảm giá")
                                    Text(
                                        text = "-${formatCurrency(order.discountAmount)}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            
                            Divider()
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tổng cộng",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formatCurrency(order.totalAmount),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (order.status) {
                            ServiceOrderStatus.PENDING -> {
                                SormsButton(
                                    onClick = { showCancelDialog = true },
                                    text = "Hủy đơn hàng",
                                    variant = ButtonVariant.Danger,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            ServiceOrderStatus.CONFIRMED -> {
                                SormsButton(
                                    onClick = { showPaymentDialog = true },
                                    text = if (uiState.paymentLoading) "Đang xử lý..." else "Thanh toán",
                                    variant = ButtonVariant.Primary,
                                    enabled = !uiState.paymentLoading,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            else -> {
                                // No actions for completed/cancelled orders
                            }
                        }
                    }
                }

                // Error Messages
                uiState.paymentError?.let { error ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Lỗi thanh toán: $error",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                uiState.cancelError?.let { error ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Lỗi hủy đơn: $error",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Payment Method Dialog
    if (showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentDialog = false },
            title = { Text("Chọn phương thức thanh toán") },
            text = {
                Column {
                    PaymentMethod.entries.forEach { method ->
                        TextButton(
                            onClick = {
                                viewModel.payOrder(order!!.id, method)
                                showPaymentDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when (method) {
                                        PaymentMethod.CASH -> Icons.Default.Money
                                        PaymentMethod.CARD -> Icons.Default.CreditCard
                                        PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
                                        PaymentMethod.WALLET -> Icons.Default.Wallet
                                    },
                                    contentDescription = null
                                )
                                Text(method.displayName)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPaymentDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Xác nhận hủy đơn hàng") },
            text = { Text("Bạn có chắc chắn muốn hủy đơn hàng này? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelOrder(order!!.id)
                        showCancelDialog = false
                    }
                ) {
                    Text("Hủy đơn hàng")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Không")
                }
            }
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
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}