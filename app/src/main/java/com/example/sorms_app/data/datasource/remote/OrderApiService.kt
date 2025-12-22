package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service for Service Orders (Invoice & Payment)
 * Maps to backend OrderController endpoints
 */
interface OrderApiService {

    // GET /orders/my-orders - Get current user's orders
    @GET("orders/my-orders")
    suspend fun getMyOrders(
        @Query("bookingId") bookingId: Long? = null
    ): Response<ApiResponse<List<ServiceOrderResponse>>>

    // GET /orders/{id} - Get order by ID
    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Long): Response<ApiResponse<ServiceOrderResponse>>

    // POST /orders/cart - Create order cart
    @POST("orders/cart")
    suspend fun createOrderCart(@Body request: CreateOrderCartRequest): Response<ApiResponse<ServiceOrderResponse>>

    // POST /orders/{orderId}/items - Add item to order
    @POST("orders/{orderId}/items")
    suspend fun addOrderItem(
        @Path("orderId") orderId: Long,
        @Body request: AddOrderItemRequest
    ): Response<ApiResponse<ServiceOrderResponse>>

    // PUT /orders/{orderId}/items/{itemId} - Update order item
    @PUT("orders/{orderId}/items/{itemId}")
    suspend fun updateOrderItem(
        @Path("orderId") orderId: Long,
        @Path("itemId") itemId: Long,
        @Body request: UpdateOrderItemRequest
    ): Response<ApiResponse<ServiceOrderResponse>>

    // DELETE /orders/{orderId}/items/{itemId} - Remove item from order
    @DELETE("orders/{orderId}/items/{itemId}")
    suspend fun removeOrderItem(
        @Path("orderId") orderId: Long,
        @Path("itemId") itemId: Long
    ): Response<ApiResponse<Void>>

    // POST /orders/{orderId}/confirm - Confirm order
    @POST("orders/{orderId}/confirm")
    suspend fun confirmOrder(@Path("orderId") orderId: Long): Response<ApiResponse<ServiceOrderResponse>>

    // POST /orders/{orderId}/cancel - Cancel order (user side)
    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(@Path("orderId") orderId: Long): Response<ApiResponse<ServiceOrderResponse>>

    // POST /payments/create - Create payment
    @POST("payments/create")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<ApiResponse<PaymentResponse>>

    // GET /payments/{transactionId} - Get payment status
    @GET("payments/{transactionId}")
    suspend fun getPaymentStatus(@Path("transactionId") transactionId: String): Response<ApiResponse<PaymentResponse>>
}

// ==================== Request Models ====================

data class CreateOrderCartRequest(
    @SerializedName("bookingId") val bookingId: Long,
    @SerializedName("requestedBy") val requestedBy: String? = null,
    @SerializedName("note") val note: String? = null
)

data class AddOrderItemRequest(
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("serviceDate") val serviceDate: String? = null,
    @SerializedName("serviceTime") val serviceTime: String? = null,
    @SerializedName("assignedStaffId") val assignedStaffId: Long? = null
)

data class UpdateOrderItemRequest(
    @SerializedName("quantity") val quantity: Int
)

data class CreatePaymentRequest(
    @SerializedName("serviceOrderId") val serviceOrderId: Long,
    @SerializedName("method") val method: String, // CASH, CARD, BANK_TRANSFER, WALLET
    @SerializedName("returnUrl") val returnUrl: String? = null,
    @SerializedName("cancelUrl") val cancelUrl: String? = null
)

// ==================== Response Models ====================

data class ServiceOrderResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("code") val code: String?,
    @SerializedName("bookingId") val bookingId: Long?,
    @SerializedName("requestedBy") val requestedBy: Long?,
    @SerializedName("status") val status: String?, // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    @SerializedName("subtotalAmount") val subtotalAmount: Double?,
    @SerializedName("discountAmount") val discountAmount: Double?,
    @SerializedName("totalAmount") val totalAmount: Double?,
    @SerializedName("note") val note: String?,
    @SerializedName("items") val items: List<ServiceOrderItemResponse>?,
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("lastModifiedDate") val lastModifiedDate: String?
)

data class ServiceOrderItemResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("serviceId") val serviceId: Long?,
    @SerializedName("serviceName") val serviceName: String?,
    @SerializedName("quantity") val quantity: Int?,
    @SerializedName("unitPrice") val unitPrice: Double?,
    @SerializedName("lineTotal") val lineTotal: Double?
)

data class PaymentResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("serviceOrderId") val serviceOrderId: Long?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("method") val method: String?,
    @SerializedName("status") val status: String?, // PENDING, SUCCEEDED, FAILED, REFUNDED
    @SerializedName("providerTxnId") val providerTxnId: String?,
    @SerializedName("paymentUrl") val paymentUrl: String?,
    @SerializedName("paidAt") val paidAt: String?,
    @SerializedName("createdDate") val createdDate: String?
)

