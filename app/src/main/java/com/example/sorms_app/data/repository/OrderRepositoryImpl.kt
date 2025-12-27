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
            val request = CreateOrderCartRequest(bookingId = bookingId, note = note)
            val response = orderApiService.createOrderCart(request)
            if (response.isSuccessful && response.body()?.data != null) {
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                kotlin.Result.failure(Exception("Không thể tạo giỏ hàng: ${response.message()}"))
            }
        } catch (e: Exception) {
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
            val request = AddOrderItemRequest(
                serviceId = serviceId,
                quantity = quantity,
                serviceTime = serviceTime
            )
            val response = orderApiService.addOrderItem(orderId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                kotlin.Result.success(response.body()!!.data!!.toDomain())
            } else {
                kotlin.Result.failure(Exception("Không thể thêm dịch vụ: ${response.message()}"))
            }
        } catch (e: Exception) {
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
                kotlin.Result.failure(Exception("Không thể hủy đơn hàng: ${response.message()}"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
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
