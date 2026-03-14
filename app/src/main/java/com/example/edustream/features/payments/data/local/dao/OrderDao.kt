package com.example.edustream.features.payments.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.edustream.features.payments.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_table ORDER BY createdAt DESC")
    fun getAllOrders(): PagingSource<Int, OrderEntity>

    @Query("SELECT * FROM order_table WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("DELETE FROM order_table")
    suspend fun deleteAllOrders()
}
