package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.domain.repository.BookingRepository
import com.example.sorms_app.domain.repository.OrderRepository
import com.example.sorms_app.domain.repository.ServiceRepository
import com.example.sorms_app.domain.repository.StaffProfileRepository
import com.example.sorms_app.presentation.components.StaffOption
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
    val staffOptions: List<StaffOption> = emptyList(),
    val isLoadingStaff: Boolean = false,
    val errorMessage: String? = null,
    // Modal state
    val isServiceOrderModalOpen: Boolean = false,
    val selectedServiceForOrder: Service? = null,
    // Submission state
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val bookingRepository: BookingRepository,
    private val orderRepository: OrderRepository,
    private val staffProfileRepository: StaffProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    init {
        loadServices()
        loadUserBookings()
        loadStaffOptions()
    }

    fun loadServices() {
        viewModelScope.launch {
            serviceRepository.getAvailableServices()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message) }
                .collect { services ->
                    _uiState.value = _uiState.value.copy(isLoading = false, services = services)
                }
        }
    }

    fun loadUserBookings() {
        viewModelScope.launch {
            bookingRepository.getUserBookings()
                .catch {
                    _uiState.value = _uiState.value.copy(userBookings = emptyList())
                }
                .collect { bookings ->
                    // Keep only CHECKED_IN bookings for service ordering (like web)
                    val checkedIn = bookings.filter { it.status.equals("CHECKED_IN", ignoreCase = true) }
                    _uiState.value = _uiState.value.copy(userBookings = checkedIn)
                }
        }
    }

    fun loadStaffOptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingStaff = true)
            val result = staffProfileRepository.getActiveStaffOptions()
            result.fold(
                onSuccess = { options ->
                    _uiState.value = _uiState.value.copy(staffOptions = options, isLoadingStaff = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(staffOptions = emptyList(), isLoadingStaff = false)
                }
            )
        }
    }

    fun openServiceOrderModal(service: Service) {
        _uiState.value = _uiState.value.copy(
            isServiceOrderModalOpen = true,
            selectedServiceForOrder = service,
            errorMessage = null
        )
    }

    fun closeServiceOrderModal() {
        _uiState.value = _uiState.value.copy(
            isServiceOrderModalOpen = false,
            selectedServiceForOrder = null,
            errorMessage = null
        )
    }

    // These are called by MultiStepServiceOrderModal

    suspend fun createOrderCart(bookingId: Long, note: String?): kotlin.Result<Long> {
        return orderRepository.createOrderCart(bookingId, note).map { it.id }
    }

    suspend fun addOrderItem(orderId: Long, serviceId: Long, quantity: Int): kotlin.Result<Unit> {
        return orderRepository.addOrderItem(orderId, serviceId, quantity).map { Unit }
    }

    suspend fun createServiceOrder(
        bookingId: Long,
        orderId: Long,
        serviceId: Long,
        quantity: Int,
        assignedStaffId: Long,
        serviceTime: String,
        note: String?
    ): kotlin.Result<Unit> {
        val requestedBy = AuthSession.accountId ?: return kotlin.Result.failure(Exception("Không tìm thấy thông tin người dùng"))
        return orderRepository.createServiceOrder(
            bookingId = bookingId,
            orderId = orderId,
            serviceId = serviceId,
            quantity = quantity,
            assignedStaffId = assignedStaffId,
            requestedBy = requestedBy,
            serviceTime = serviceTime,
            note = note
        ).map { Unit }
    }

    fun createServiceRequestWithBooking(
        serviceId: String,
        bookingId: Long,
        quantity: Int,
        serviceDate: String,
        notes: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, submissionSuccess = false)
            // TODO: Implement service request creation with booking
            // For now, just simulate success
            _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = true)
        }
    }

    fun createServiceRequest(
        serviceId: String,
        notes: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, submissionSuccess = false)
            // TODO: Implement service request creation without booking
            // For now, just simulate success
            _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = true)
        }
    }

    fun resetSubmissionState() {
        _uiState.value = _uiState.value.copy(submissionSuccess = false, errorMessage = null)
    }
}