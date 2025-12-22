package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.presentation.screens.user.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val totalItems: Int = 0,
    val isCheckingOut: Boolean = false,
    val checkoutSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    // Add repositories when needed
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // TODO: Load cart from local storage or API
                // For now, simulate empty cart
                val cartItems = emptyList<CartItem>()
                updateCartSummary(cartItems)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        cartItems = cartItems
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Không thể tải giỏ hàng"
                    ) 
                }
            }
        }
    }

    fun updateQuantity(serviceId: String, newQuantity: Int) {
        val currentItems = _uiState.value.cartItems.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.service.id == serviceId }
        
        if (itemIndex != -1) {
            currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = newQuantity)
            updateCartSummary(currentItems)
            
            _uiState.update { 
                it.copy(cartItems = currentItems) 
            }
        }
    }

    fun removeFromCart(serviceId: String) {
        val currentItems = _uiState.value.cartItems.toMutableList()
        currentItems.removeAll { it.service.id == serviceId }
        updateCartSummary(currentItems)
        
        _uiState.update { 
            it.copy(cartItems = currentItems) 
        }
    }

    fun clearCart() {
        _uiState.update { 
            it.copy(
                cartItems = emptyList(),
                subtotal = 0.0,
                total = 0.0,
                totalItems = 0
            ) 
        }
    }

    fun checkout() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isCheckingOut = true,
                    errorMessage = null,
                    checkoutSuccess = false
                ) 
            }
            
            try {
                // TODO: Implement checkout logic
                // Create order from cart items
                kotlinx.coroutines.delay(2000) // Simulate API call
                
                _uiState.update { 
                    it.copy(
                        isCheckingOut = false,
                        checkoutSuccess = true,
                        cartItems = emptyList(),
                        subtotal = 0.0,
                        total = 0.0,
                        totalItems = 0
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCheckingOut = false,
                        errorMessage = e.message ?: "Không thể thanh toán"
                    ) 
                }
            }
        }
    }

    private fun updateCartSummary(cartItems: List<CartItem>) {
        val subtotal = cartItems.sumOf { it.service.unitPrice * it.quantity }
        val totalItems = cartItems.sumOf { it.quantity }
        
        _uiState.update { 
            it.copy(
                subtotal = subtotal,
                total = subtotal, // No additional fees for now
                totalItems = totalItems
            ) 
        }
    }

    fun clearCheckoutState() {
        _uiState.update { 
            it.copy(
                checkoutSuccess = false,
                errorMessage = null
            ) 
        }
    }
}