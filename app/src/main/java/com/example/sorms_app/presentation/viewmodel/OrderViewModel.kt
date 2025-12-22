package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.PaymentMethod
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.model.ServiceOrderStatus
import com.example.sorms_app.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderUiState(
    val isLoading: Boolean = false,
    val orders: List<ServiceOrder> = emptyList(),
    val errorMessage: String? = null,
    val selectedOrder: ServiceOrder? = null,
    val paymentLoading: Boolean = false,
    val paymentUrl: String? = null,
    val paymentSuccess: Boolean = false,
    val paymentError: String? = null,
    val cancelSuccess: Boolean = false,
    val cancelError: String? = null
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders(bookingId: Long? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            orderRepository.getMyOrders(bookingId)
                .onSuccess { orders ->
                    // Sort by created date descending (newest first)
                    val sortedOrders = orders.sortedByDescending { it.createdDate }
                    _uiState.update { it.copy(isLoading = false, orders = sortedOrders) }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = error.message ?: "Không thể tải danh sách hóa đơn"
                        ) 
                    }
                }
        }
    }

    fun selectOrder(order: ServiceOrder?) {
        _uiState.update { it.copy(selectedOrder = order) }
    }

    fun payOrder(orderId: Long, method: PaymentMethod = PaymentMethod.BANK_TRANSFER) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    paymentLoading = true, 
                    paymentError = null,
                    paymentSuccess = false,
                    paymentUrl = null
                ) 
            }
            
            orderRepository.createPayment(orderId, method)
                .onSuccess { payment ->
                    if (payment.paymentUrl != null) {
                        // If there's a payment URL, we need to redirect
                        _uiState.update { 
                            it.copy(
                                paymentLoading = false, 
                                paymentUrl = payment.paymentUrl
                            ) 
                        }
                    } else {
                        // Payment created successfully without redirect
                        _uiState.update { 
                            it.copy(
                                paymentLoading = false, 
                                paymentSuccess = true
                            ) 
                        }
                        // Reload orders to reflect new status
                        loadOrders()
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            paymentLoading = false, 
                            paymentError = error.message ?: "Không thể tạo thanh toán"
                        ) 
                    }
                }
        }
    }

    fun clearPaymentState() {
        _uiState.update { 
            it.copy(
                paymentUrl = null,
                paymentSuccess = false,
                paymentError = null
            ) 
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cancelSuccess = false, cancelError = null) }
            
            orderRepository.cancelOrder(orderId)
                .onSuccess {
                    _uiState.update { it.copy(cancelSuccess = true) }
                    loadOrders()
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(cancelError = error.message ?: "Không thể hủy đơn hàng") 
                    }
                }
        }
    }

    fun clearCancelState() {
        _uiState.update { it.copy(cancelSuccess = false, cancelError = null) }
    }

    fun refresh() {
        loadOrders()
    }

    // Filter orders by status
    fun getOrdersByStatus(status: ServiceOrderStatus): List<ServiceOrder> {
        return _uiState.value.orders.filter { it.status == status }
    }

    // Get pending orders (unpaid)
    fun getPendingOrders(): List<ServiceOrder> {
        return _uiState.value.orders.filter { 
            it.status != ServiceOrderStatus.COMPLETED && it.status != ServiceOrderStatus.CANCELLED 
        }
    }

    // Get completed orders
    fun getCompletedOrders(): List<ServiceOrder> {
        return _uiState.value.orders.filter { 
            it.status == ServiceOrderStatus.COMPLETED 
        }
    }
}

