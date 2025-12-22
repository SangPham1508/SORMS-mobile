package com.example.sorms_app.domain.model

/**
 * Domain model for Service Order (Invoice)
 */
data class ServiceOrder(
    val id: Long,
    val code: String,
    val bookingId: Long?,
    val status: ServiceOrderStatus,
    val subtotalAmount: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val note: String?,
    val items: List<ServiceOrderItem>,
    val createdDate: String?,
    val lastModifiedDate: String?
)

data class ServiceOrderItem(
    val id: Long,
    val serviceId: Long?,
    val serviceName: String,
    val quantity: Int,
    val unitPrice: Double,
    val lineTotal: Double
)

enum class ServiceOrderStatus(val displayName: String) {
    PENDING("Chờ xử lý"),
    CONFIRMED("Đã xác nhận"),
    IN_PROGRESS("Đang xử lý"),
    COMPLETED("Đã hoàn thành"),
    CANCELLED("Đã hủy");

    companion object {
        fun fromString(value: String?): ServiceOrderStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

enum class PaymentMethod(val displayName: String) {
    CASH("Tiền mặt"),
    CARD("Thẻ"),
    BANK_TRANSFER("Chuyển khoản"),
    WALLET("Ví điện tử")
}

enum class PaymentStatus(val displayName: String) {
    PENDING("Chờ thanh toán"),
    SUCCEEDED("Đã thanh toán"),
    FAILED("Thất bại"),
    REFUNDED("Đã hoàn tiền");

    companion object {
        fun fromString(value: String?): PaymentStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

data class Payment(
    val id: Long?,
    val serviceOrderId: Long?,
    val amount: Double,
    val currency: String,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val paymentUrl: String?,
    val paidAt: String?,
    val createdDate: String?
)

