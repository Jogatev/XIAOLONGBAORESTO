package com.css152lgroup10.noodlemoneybuddy.data.models

import androidx.room.*

@Dao
interface OrderDao {
    // OrderRecord CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderRecord)

    @Update
    suspend fun updateOrder(order: OrderRecord)

    @Delete
    suspend fun deleteOrder(order: OrderRecord)

    @Query("SELECT * FROM order_records ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<OrderRecord>

    // OrderItem CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItem)

    @Update
    suspend fun updateOrderItem(item: OrderItem)

    @Delete
    suspend fun deleteOrderItem(item: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: String): List<OrderItem>

    // Order with items
    @Transaction
    @Query("SELECT * FROM order_records ORDER BY timestamp DESC")
    suspend fun getAllOrdersWithItems(): List<OrderWithItems>

    @Transaction
    @Query("SELECT * FROM order_records WHERE id = :orderId")
    suspend fun getOrderWithItems(orderId: String): OrderWithItems?
} 