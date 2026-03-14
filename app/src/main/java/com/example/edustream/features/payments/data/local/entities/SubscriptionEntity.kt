package com.example.edustream.features.payments.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlanType {
    MONTHLY, ANNUAL
}

enum class SubStatus {
    ACTIVE, EXPIRED, CANCELLED, GRACE_PERIOD
}

@Entity(tableName = "subscription_table")
data class SubscriptionEntity(
    @PrimaryKey
    val subscriptionId: String,
    val userId: String,
    val planType: PlanType,
    val status: SubStatus,
    val startDate: Long,
    val endDate: Long,
    val autoRenew: Boolean,
    val playPurchaseToken: String
)
