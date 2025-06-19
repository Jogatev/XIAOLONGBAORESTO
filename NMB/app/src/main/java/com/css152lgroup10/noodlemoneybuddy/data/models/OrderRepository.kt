package com.css152lgroup10.noodlemoneybuddy.data.models

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepository private constructor(context: Context) {
    private val db = OrderDatabase.getDatabase(context)
    private val dao = db.orderDao()

    // Singleton pattern
    companion object {
        @Volatile private var INSTANCE: OrderRepository? = null
        fun getInstance(context: Context): OrderRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = OrderRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    // Orders with items as Flow
    fun getAllOrdersWithItemsFlow(): Flow<List<OrderWithItems>> = flow {
        emit(dao.getAllOrdersWithItems())
    }

    suspend fun getOrderWithItems(orderId: String): OrderWithItems? = dao.getOrderWithItems(orderId)

    suspend fun insertOrderWithItems(order: OrderRecord, items: List<OrderItem>) {
        dao.insertOrder(order)
        items.forEach { dao.insertOrderItem(it) }
    }

    suspend fun updateOrder(order: OrderRecord) = dao.updateOrder(order)
    suspend fun deleteOrder(order: OrderRecord) = dao.deleteOrder(order)
    suspend fun insertOrderItem(item: OrderItem) = dao.insertOrderItem(item)
    suspend fun updateOrderItem(item: OrderItem) = dao.updateOrderItem(item)
    suspend fun deleteOrderItem(item: OrderItem) = dao.deleteOrderItem(item)
    suspend fun getItemsForOrder(orderId: String): List<OrderItem> = dao.getItemsForOrder(orderId)
} 