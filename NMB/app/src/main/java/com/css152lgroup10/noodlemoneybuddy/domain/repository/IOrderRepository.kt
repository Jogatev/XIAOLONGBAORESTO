package com.css152lgroup10.noodlemoneybuddy.domain.repository

import com.css152lgroup10.noodlemoneybuddy.data.model.*
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {

    fun getAllOrders(): Flow<List<Order>>

    suspend fun getOrderById(orderId: String): Order?

    suspend fun createOrder(order: Order): Result<Order>

    suspend fun updateOrder(order: Order): Result<Order>

    suspend fun deleteOrder(orderId: String): Result<Boolean>

    suspend fun saveOrder(order: Order): Result<Order>

    suspend fun getMenuItems(): List<MenuItem>

    suspend fun getMenuItemById(itemId: String): MenuItem?

    suspend fun addMenuItem(menuItem: MenuItem): Result<MenuItem>

    suspend fun updateMenuItem(menuItem: MenuItem): Result<MenuItem>

    suspend fun deleteMenuItem(itemId: String): Result<Boolean>

    suspend fun getSalesStatistics(
        startDate: String,
        endDate: String
    ): Result<SalesStatistics>

    suspend fun getDailySalesData(
        startDate: String,
        endDate: String
    ): Result<List<SalesData>>

    suspend fun exportOrdersToCSV(
        startDate: String? = null,
        endDate: String? = null
    ): Result<String>

    suspend fun exportSalesStatisticsToCSV(
        startDate: String,
        endDate: String
    ): Result<String>

    suspend fun searchOrders(
        query: String,
        status: OrderStatus? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<Order>>

    suspend fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>

    suspend fun getOrdersByDate(date: String): Result<List<Order>>

    suspend fun createBackup(): Result<String>

    suspend fun restoreFromBackup(backupData: String): Result<Boolean>


    suspend fun clearCache(): Result<Boolean>

    suspend fun refreshCache(): Result<Boolean>
}