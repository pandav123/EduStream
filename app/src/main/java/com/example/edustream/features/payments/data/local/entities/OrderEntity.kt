package com.example.edustream.features.payments.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus {
    PENDING, SUCCESS, FAILED, REFUNDED
}

@Entity(tableName = "order_table")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val razorpayOrderId: String,
    val courseId: String,
    val amount: Double,
    val currency: String,
    val status: OrderStatus,
    val paymentId: String?,
    val couponCode: String?,
    val discountAmount: Double,
    val createdAt: Long
)
