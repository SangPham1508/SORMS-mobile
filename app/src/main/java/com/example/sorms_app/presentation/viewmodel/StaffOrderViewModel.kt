package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.ServiceOrder
import com.example.sorms_app.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StaffOrderUiState(
    val isLoading: Boolean = false,
    val orders: List<ServiceOrder> = emptyList(),
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateError: String? = null
)

@HiltViewModel
class StaffOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffOrderUiState())
    val uiState: StateFlow<StaffOrderUiState> = _uiState.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Load all orders for staff management
            orderRepository.getMyOrders() // This should be changed to getAllOrders for staff
                .onSuccess { orders ->
                    // Sort by created date descending (newest first)
                    val sortedOrders = orders.sortedByDescending { it.createdDate }
                    _uiState.update { it.copy(isLoading = false, orders = sortedOrders) }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = error.message ?: "Không thể tải danh sách đơn hàng"
                        ) 
                    }
                }
        }
    }

    fun confirmOrder(orderId: Long) {
        updateOrderStatus(orderId, "CONFIRMED")
    }

    fun startOrder(orderId: Long) {
        updateOrderStatus(orderId, "IN_PROGRESS")
    }

    fun completeOrder(orderId: Long) {
        updateOrderStatus(orderId, "COMPLETED")
    }

    private fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isUpdating = true,
                    updateError = null,
                    updateSuccess = false
                ) 
            }
            
            try {
                // TODO: Implement order status update API call
                // For now, simulate the update
                kotlinx.coroutines.delay(1000)
                
                // Update local state
                val updatedOrders = _uiState.value.orders.map { order ->
                    if (order.id == orderId) {
                        order.copy(status = com.example.sorms_app.domain.model.ServiceOrderStatus.fromString(newStatus))
                    } else {
                        order
                    }
                }
                
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        updateSuccess = true,
                        orders = updatedOrders
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        updateError = e.message ?: "Không thể cập nhật trạng thái đơn hàng"
                    ) 
                }
            }
        }
    }

    fun refresh() {
        loadOrders()
    }

    fun clearUpdateState() {
        _uiState.update { 
            it.copy(
                updateSuccess = false,
                updateError = null
            ) 
        }
    }
}