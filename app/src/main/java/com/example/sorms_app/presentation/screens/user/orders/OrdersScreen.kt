package com.example.sorms_app.presentation.screens.user.orders

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.PaymentMethod
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.model.ServiceOrderStatus
import com.example.sorms_app.presentation.components.BadgeTone
import com.example.sorms_app.presentation.components.SormsBadge
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(viewModel: OrderViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showPaymentDialog by remember { mutableStateOf(false) }
    var selectedOrderForPayment by remember { mutableStateOf<ServiceOrder?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedOrderForCancel by remember { mutableStateOf<ServiceOrder?>(null) }
    var showPayAllDialog by remember { mutableStateOf(false) }
    
    // Handle payment URL redirect
    LaunchedEffect(uiState.paymentUrl) {
        uiState.paymentUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            viewModel.clearPaymentState()
        }
    }

    // Show success/error messages
    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) {
            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
            viewModel.clearPaymentState()
        }
    }

    LaunchedEffect(uiState.paymentError) {
        uiState.paymentError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearPaymentState()
        }
    }

    LaunchedEffect(uiState.cancelSuccess) {
        if (uiState.cancelSuccess) {
            Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show()
            viewModel.clearCancelState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Hóa đơn & Thanh toán")
                        Text(
                            text = "Quản lý hóa đơn dịch vụ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SormsLoading(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải danh sách hóa đơn.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.orders.isEmpty() -> {
                SormsEmptyState(
                    title = "Chưa có hóa đơn",
                    subtitle = "Danh sách hóa đơn dịch vụ của bạn sẽ được hiển thị ở đây.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Summary card for combined payment
                    val unpaidOrders = uiState.orders.filter { 
                        it.status != ServiceOrderStatus.COMPLETED && 
                        it.status != ServiceOrderStatus.CANCELLED 
                    }
                    val totalUnpaid = unpaidOrders.sumOf { it.totalAmount }
                    
                    if (unpaidOrders.isNotEmpty()) {
                        PaymentSummaryCard(
                            totalUnpaid = totalUnpaid,
                            orderCount = unpaidOrders.size,
                            onPayAll = { showPayAllDialog = true },
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    OrderList(
                        orders = uiState.orders,
                        onPayClick = { order ->
                            selectedOrderForPayment = order
                            showPaymentDialog = true
                        },
                        onCancelClick = { order ->
                            selectedOrderForCancel = order
                            showCancelDialog = true
                        },
                        onDownloadClick = { order ->
                            Toast.makeText(context, "Tính năng tải hóa đơn đang phát triển", Toast.LENGTH_SHORT).show()
                        },
                        isPaymentLoading = uiState.paymentLoading,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Payment Method Dialog
        if (showPaymentDialog && selectedOrderForPayment != null) {
            PaymentMethodDialog(
                order = selectedOrderForPayment!!,
                onDismiss = { 
                    showPaymentDialog = false
                    selectedOrderForPayment = null
                },
                onConfirm = { method ->
                    viewModel.payOrder(selectedOrderForPayment!!.id, method)
                    showPaymentDialog = false
                    selectedOrderForPayment = null
                }
            )
        }

        // Cancel Order Dialog
        if (showCancelDialog && selectedOrderForCancel != null) {
            AlertDialog(
                onDismissRequest = {
                    showCancelDialog = false
                    selectedOrderForCancel = null
                },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("Xác nhận hủy đơn hàng") },
                text = {
                    Text("Bạn có chắc chắn muốn hủy đơn hàng ${selectedOrderForCancel?.code}?\n\nChỉ có thể hủy đơn hàng khi chưa được staff xác nhận.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelOrder(selectedOrderForCancel!!.id)
                            showCancelDialog = false
                            selectedOrderForCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Hủy đơn hàng")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCancelDialog = false
                        selectedOrderForCancel = null
                    }) {
                        Text("Đóng")
                    }
                }
            )
        }

        // Pay All Dialog
        if (showPayAllDialog) {
            val unpaidOrders = uiState.orders.filter { 
                it.status != ServiceOrderStatus.COMPLETED && 
                it.status != ServiceOrderStatus.CANCELLED 
            }
            val totalUnpaid = unpaidOrders.sumOf { it.totalAmount }
            val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

            AlertDialog(
                onDismissRequest = { showPayAllDialog = false },
                icon = { Icon(Icons.Default.Payment, contentDescription = null) },
                title = { Text("Thanh toán tất cả") },
                text = {
                    Column {
                        Text("Thanh toán tổng hợp ${unpaidOrders.size} đơn hàng:")
                        Spacer(Modifier.height(8.dp))
                        unpaidOrders.forEach { order ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(order.code, style = MaterialTheme.typography.bodySmall)
                                Text(currencyFormat.format(order.totalAmount), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tổng cộng:", fontWeight = FontWeight.Bold)
                            Text(
                                currencyFormat.format(totalUnpaid), 
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Pay all orders sequentially or combined
                            // For now, pay the first one (in real app, backend would handle combined payment)
                            unpaidOrders.firstOrNull()?.let { order ->
                                viewModel.payOrder(order.id, PaymentMethod.BANK_TRANSFER)
                            }
                            showPayAllDialog = false
                        }
                    ) {
                        Text("Thanh toán ngay")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPayAllDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
private fun PaymentSummaryCard(
    totalUnpaid: Double,
    orderCount: Int,
    onPayAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tổng chưa thanh toán",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = currencyFormat.format(totalUnpaid),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$orderCount đơn hàng",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Button(
                onClick = onPayAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Thanh toán tất cả")
            }
        }
    }
}

@Composable
private fun OrderList(
    orders: List<ServiceOrder>,
    onPayClick: (ServiceOrder) -> Unit,
    onCancelClick: (ServiceOrder) -> Unit,
    onDownloadClick: (ServiceOrder) -> Unit,
    isPaymentLoading: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders) { order ->
            OrderCard(
                order = order,
                onPayClick = { onPayClick(order) },
                onCancelClick = { onCancelClick(order) },
                onDownloadClick = { onDownloadClick(order) },
                isPaymentLoading = isPaymentLoading
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: ServiceOrder,
    onPayClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDownloadClick: () -> Unit,
    isPaymentLoading: Boolean
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
    
    val canPay = order.status != ServiceOrderStatus.COMPLETED && 
                 order.status != ServiceOrderStatus.CANCELLED
    val canCancel = order.status == ServiceOrderStatus.PENDING || 
                   order.status == ServiceOrderStatus.CONFIRMED

    SormsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Code + Status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = order.code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                SormsBadge(
                    text = order.status.displayName,
                    tone = when (order.status) {
                        ServiceOrderStatus.COMPLETED -> BadgeTone.Success
                        ServiceOrderStatus.CANCELLED -> BadgeTone.Error
                        ServiceOrderStatus.IN_PROGRESS -> BadgeTone.Warning
                        ServiceOrderStatus.CONFIRMED -> BadgeTone.Default
                        else -> BadgeTone.Default
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            // Order items summary
            if (order.items.isNotEmpty()) {
                Text(
                    text = "Chi tiết dịch vụ:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                order.items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${item.serviceName} x${item.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = currencyFormat.format(item.lineTotal),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Divider()
                Spacer(Modifier.height(8.dp))
            }

            // Total amount
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tổng cộng:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currencyFormat.format(order.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Created date
            order.createdDate?.let { dateStr ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Ngày tạo: $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status note for editable orders
            if (canCancel) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (order.status == ServiceOrderStatus.PENDING) 
                                "Có thể hủy trước khi staff xác nhận" 
                            else "Đã xác nhận - có thể hủy trước khi xử lý",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (canCancel) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Hủy")
                    }
                }

                if (canPay) {
                    Button(
                        onClick = onPayClick,
                        enabled = !isPaymentLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isPaymentLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Thanh toán")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodDialog(
    order: ServiceOrder,
    onDismiss: () -> Unit,
    onConfirm: (PaymentMethod) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Thanh toán hóa đơn",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Mã hóa đơn: ${order.code}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Số tiền: ${currencyFormat.format(order.totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Chọn phương thức thanh toán:",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(8.dp))
                
                PaymentMethod.entries.forEach { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = method.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedMethod) }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
