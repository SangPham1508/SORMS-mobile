package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.domain.repository.BookingRepository
import com.example.sorms_app.domain.repository.OrderRepository
import com.example.sorms_app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val userBookings: List<Booking> = emptyList(),
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val bookingRepository: BookingRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            serviceRepository.getAvailableServices()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, submissionSuccess = false, errorMessage = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message) }
                .collect { services ->
                    _uiState.value = _uiState.value.copy(isLoading = false, services = services)
                }
        }
    }

    fun loadUserBookings() {
        viewModelScope.launch {
            bookingRepository.getUserBookings()
                .onStart { /* Don't show loading for bookings */ }
                .catch { e -> 
                    // Don't fail the whole screen for booking load error
                    _uiState.value = _uiState.value.copy(userBookings = emptyList())
                }
                .collect { bookings ->
                    _uiState.value = _uiState.value.copy(userBookings = bookings)
                }
        }
    }

    fun createServiceRequest(serviceId: String, notes: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, submissionSuccess = false)
            try {
                serviceRepository.createServiceRequest(serviceId, notes)
                _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = e.message)
            }
        }
    }

    /**
     * Create service request with booking - follows the cart workflow:
     * 1. Create order cart for the booking
     * 2. Add service item to the cart
     * 3. Confirm the order
     */
    fun createServiceRequestWithBooking(
        serviceId: String,
        bookingId: Long,
        quantity: Int,
        serviceDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, submissionSuccess = false)
            
            try {
                // Step 1: Create order cart
                val cartResult = orderRepository.createOrderCart(bookingId, notes)
                val cart = cartResult.getOrThrow()
                
                // Step 2: Add service item to cart
                val addItemResult = orderRepository.addOrderItem(
                    orderId = cart.id,
                    serviceId = serviceId.toLongOrNull() ?: throw Exception("Invalid service ID"),
                    quantity = quantity,
                    serviceTime = serviceDate
                )
                addItemResult.getOrThrow()
                
                // Step 3: Confirm the order
                val confirmResult = orderRepository.confirmOrder(cart.id)
                confirmResult.getOrThrow()
                
                _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false, 
                    errorMessage = e.message ?: "Không thể tạo yêu cầu dịch vụ"
                )
            }
        }
    }
    
    fun resetSubmissionState() {
        _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = false, errorMessage = null)
    }
}
