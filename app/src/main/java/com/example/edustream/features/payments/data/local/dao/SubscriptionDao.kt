package com.example.edustream.features.payments.data.local.dao

import androidx.room.*
import com.example.edustream.features.payments.data.local.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscription_table WHERE userId = :userId LIMIT 1")
    fun getSubscriptionByUserId(userId: String): Flow<SubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscription_table")
    suspend fun deleteSubscription()
}
