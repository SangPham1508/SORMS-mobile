package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.remote.*
import com.example.sorms_app.domain.model.*
import com.example.sorms_app.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderApiService: OrderApiService
) : OrderRepository {

    override suspend fun getMyOrders(bookingId: Long?): kotlin.Result<List<ServiceOrder>> = withContext(Dispatchers.IO) {
        try {
            val response = orderApiService.getMyOrders(bookingId)
            if (response.isSuccessful) {
                val orders = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                kotlin.Result.success(orders)
            } else {
                kotlin.Result.failure(Exception("Không thể tải danh sách đơn hàng: ${response.code()}"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    override suspend fun getOrderById(id: Long): kotlin.Result<ServiceOrder> = withContext(Dispatchers.IO) {
        try {
            val response = orderApiService.getOrderById(id)
            if (response.isSuccessful && response.body()?.data != null) {
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                kotlin.Result.failure(Exception("Không tìm thấy đơn hàng"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    override suspend fun createOrderCart(bookingId: Long, note: String?): kotlin.Result<ServiceOrder> = withContext(Dispatchers.IO) {
        try {
            // Đồng bộ với web: gửi requestedBy từ AuthSession
            val requestedBy = com.example.sorms_app.data.datasource.local.AuthSession.accountId
            val request = CreateOrderCartRequest(
                bookingId = bookingId,
                requestedBy = requestedBy,
                note = note
            )
            val response = orderApiService.createOrderCart(request)
            if (response.isSuccessful && response.body()?.data != null) {
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Không thể tạo giỏ hàng: ${response.code()} - ${response.message()}"
                android.util.Log.e("OrderRepository", "Create cart error: $errorMessage")
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error creating cart", e)
            kotlin.Result.failure(e)
        }
    }

    override suspend fun addOrderItem(
        orderId: Long,
        serviceId: Long,
        quantity: Int,
        serviceTime: String?
    ): kotlin.Result<ServiceOrder> = withContext(Dispatchers.IO) {
        try {
            // Đồng bộ với web: Backend AddOrderItemRequest chỉ nhận orderId, serviceId, quantity
            // serviceTime và assignedStaffId sẽ được set trong createServiceOrder
            val request = AddOrderItemRequest(
                serviceId = serviceId,
                quantity = quantity,
                serviceTime = null, // Không gửi trong addOrderItem (web không gửi)
                serviceDate = null,
                assignedStaffId = null // Không gửi trong addOrderItem (web không gửi)
            )
            val response = orderApiService.addOrderItem(orderId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                android.util.Log.d("OrderRepository", "Added item to order $orderId")
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Không thể thêm dịch vụ: ${response.code()} - ${response.message()}"
                android.util.Log.e("OrderRepository", "Add item error: $errorMessage")
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error adding item", e)
            kotlin.Result.failure(e)
        }
    }

    override suspend fun confirmOrder(orderId: Long): kotlin.Result<ServiceOrder> = withContext(Dispatchers.IO) {
        try {
            val request = ConfirmOrderRequest(orderId = orderId, note = null)
            val response = orderApiService.confirmOrder(orderId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                kotlin.Result.failure(Exception("Không thể xác nhận đơn hàng: ${response.message()}"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    override suspend fun createPayment(
        orderId: Long,
        method: PaymentMethod
    ): kotlin.Result<Payment> = withContext(Dispatchers.IO) {
        try {
            val request = CreatePaymentRequest(
                serviceOrderId = orderId,
                method = method.name
            )
            val response = orderApiService.createPayment(request)
            if (response.isSuccessful && response.body()?.data != null) {
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                kotlin.Result.failure(Exception("Không thể tạo thanh toán: ${response.message()}"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    override suspend fun cancelOrder(orderId: Long): kotlin.Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = orderApiService.cancelOrder(orderId)
            if (response.isSuccessful) {
                kotlin.Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Không thể hủy đơn hàng: ${response.code()} - ${response.message()}"
                android.util.Log.e("OrderRepository", "Cancel order error: $errorMessage")
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error canceling order", e)
            kotlin.Result.failure(e)
        }
    }

    // Đồng bộ với web: implement createServiceOrder
    override suspend fun createServiceOrder(
        bookingId: Long,
        orderId: Long,
        serviceId: Long,
        quantity: Int,
        assignedStaffId: Long,
        requestedBy: String,
        serviceTime: String,
        note: String?
    ): kotlin.Result<ServiceOrder> = withContext(Dispatchers.IO) {
        try {
            // Format serviceTime giống web: YYYY-MM-DDTHH:mm:ss (bỏ timezone nếu có)
            val formattedServiceTime = formatServiceTime(serviceTime)
            
            // Đồng bộ với web: Backend CreateServiceOrderRequest yêu cầu tất cả các fields
            val request = CreateServiceOrderRequest(
                bookingId = bookingId,
                orderId = orderId, // REQUIRED - must be existing order ID
                serviceId = serviceId,
                quantity = java.math.BigDecimal(quantity), // Backend yêu cầu BigDecimal
                assignedStaffId = assignedStaffId, // REQUIRED
                requestedBy = requestedBy, // REQUIRED
                serviceTime = formattedServiceTime, // LocalDateTime format: yyyy-MM-dd'T'HH:mm:ss
                note = note
            )
            
            val response = orderApiService.createServiceOrder(request)
            if (response.isSuccessful && response.body()?.data != null) {
                android.util.Log.d("OrderRepository", "Created service order for order $orderId")
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Không thể tạo đơn dịch vụ: ${response.code()} - ${response.message()}"
                android.util.Log.e("OrderRepository", "Create service order error: $errorMessage")
                
                // Đồng bộ với web: nếu lỗi "item not found", có thể retry addOrderItem
                if (errorMessage.contains("item not found", ignoreCase = true) || 
                    errorMessage.contains("ORDER_ITEM_NOT_FOUND", ignoreCase = true)) {
                    android.util.Log.w("OrderRepository", "Item not found, may need to retry addOrderItem")
                }
                
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error creating service order", e)
            kotlin.Result.failure(e)
        }
    }
    
    // Helper function để format serviceTime giống web
    private fun formatServiceTime(serviceTime: String): String {
        // Bỏ timezone 'Z' và milliseconds nếu có
        var formatted = serviceTime.replace(Regex("Z$"), "")
        formatted = formatted.replace(Regex("\\.\\d+$"), "")
        
        // Đảm bảo có seconds
        if (formatted.matches(Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$"))) {
            formatted = "$formatted:00"
        }
        
        return formatted
    }

    // Extension functions to convert API response to domain model
    private fun ServiceOrderResponse.toDomain(): ServiceOrder {
        return ServiceOrder(
            id = id,
            code = code ?: "ORD-$id",
            bookingId = bookingId,
            status = ServiceOrderStatus.fromString(status),
            subtotalAmount = subtotalAmount ?: 0.0,
            discountAmount = discountAmount ?: 0.0,
            totalAmount = totalAmount ?: 0.0,
            note = note,
            items = items?.map { it.toDomain() } ?: emptyList(),
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }

    private fun ServiceOrderItemResponse.toDomain(): ServiceOrderItem {
        return ServiceOrderItem(
            id = id,
            serviceId = serviceId,
            serviceName = serviceName ?: "Dịch vụ",
            quantity = quantity ?: 1,
            unitPrice = unitPrice ?: 0.0,
            lineTotal = lineTotal ?: 0.0
        )
    }

    private fun PaymentResponse.toDomain(): Payment {
        return Payment(
            id = id,
            serviceOrderId = serviceOrderId,
            amount = amount ?: 0.0,
            currency = currency ?: "VND",
            method = PaymentMethod.entries.find { it.name.equals(method, ignoreCase = true) } ?: PaymentMethod.CASH,
            status = PaymentStatus.fromString(status),
            paymentUrl = paymentUrl,
            paidAt = paidAt,
            createdDate = createdDate
        )
    }
}
