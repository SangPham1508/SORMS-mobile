package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Payment
import com.example.sorms_app.domain.model.PaymentMethod
import com.example.sorms_app.domain.model.ServiceOrder

/**
 * Repository interface for Service Orders (Invoice & Payment)
 */
interface OrderRepository {
    
    /**
     * Get current user's service orders
     * @param bookingId Optional booking ID to filter orders
     */
    suspend fun getMyOrders(bookingId: Long? = null): Result<List<ServiceOrder>>

    /**
     * Get order by ID
     */
    suspend fun getOrderById(id: Long): Result<ServiceOrder>

    /**
     * Create a new order cart for a booking
     */
    suspend fun createOrderCart(bookingId: Long, note: String? = null): Result<ServiceOrder>

    /**
     * Add a service item to an order
     */
    suspend fun addOrderItem(
        orderId: Long,
        serviceId: Long,
        quantity: Int,
        serviceTime: String? = null
    ): Result<ServiceOrder>

    /**
     * Confirm an order (submit for processing)
     */
    suspend fun confirmOrder(orderId: Long): Result<ServiceOrder>

    /**
     * Create a payment for an order
     */
    suspend fun createPayment(orderId: Long, method: PaymentMethod): Result<Payment>

    /**
     * Cancel an order (only before staff confirms)
     */
    suspend fun cancelOrder(orderId: Long): Result<Unit>
}

