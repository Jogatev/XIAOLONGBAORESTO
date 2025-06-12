package com.css152lgroup10.noodlemoneybuddy.data.repository

import com.css152lgroup10.noodlemoneybuddy.data.model.*
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor() : IOrderRepository {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    private val _menuItems = MutableStateFlow<List<MenuItem>>(getDefaultMenuItems())

    init {
        initializeSampleData()
    }

    override fun getAllOrders(): Flow<List<Order>> = _orders.asStateFlow()

    override suspend fun getOrderById(orderId: String): Order? {
        return _orders.value.find { it.id == orderId }
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            val currentOrders = _orders.value.toMutableList()
            currentOrders.add(order)
            _orders.value = currentOrders
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateOrder(order: Order): Result<Order> {
        return try {
            val currentOrders = _orders.value.toMutableList()
            val index = currentOrders.indexOfFirst { it.id == order.id }
            if (index != -1) {
                currentOrders[index] = order
                _orders.value = currentOrders
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteOrder(orderId: String): Result<Boolean> {
        return try {
            val currentOrders = _orders.value.toMutableList()
            val removed = currentOrders.removeAll { it.id == orderId }
            if (removed) {
                _orders.value = currentOrders
                Result.success(true)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveOrder(order: Order): Result<Order> {
        return if (_orders.value.any { it.id == order.id }) {
            updateOrder(order)
        } else {
            createOrder(order)
        }
    }
    override suspend fun getMenuItems(): List<MenuItem> = _menuItems.value

    override suspend fun getMenuItemById(itemId: String): MenuItem? {
        return _menuItems.value.find { it.id == itemId }
    }

    override suspend fun addMenuItem(menuItem: MenuItem): Result<MenuItem> {
        return try {
            val currentItems = _menuItems.value.toMutableList()
            currentItems.add(menuItem)
            _menuItems.value = currentItems
            Result.success(menuItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMenuItem(menuItem: MenuItem): Result<MenuItem> {
        return try {
            val currentItems = _menuItems.value.toMutableList()
            val index = currentItems.indexOfFirst { it.id == menuItem.id }
            if (index != -1) {
                currentItems[index] = menuItem
                _menuItems.value = currentItems
                Result.success(menuItem)
            } else {
                Result.failure(Exception("Menu item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMenuItem(itemId: String): Result<Boolean> {
        return try {
            val currentItems = _menuItems.value.toMutableList()
            val removed = currentItems.removeAll { it.id == itemId }
            if (removed) {
                _menuItems.value = currentItems
                Result.success(true)
            } else {
                Result.failure(Exception("Menu item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getSalesStatistics(
        startDate: String,
        endDate: String
    ): Result<SalesStatistics> {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = dateFormat.parse(startDate)?.time ?: 0L
            val end = dateFormat.parse(endDate)?.time ?: Long.MAX_VALUE

            val filteredOrders = _orders.value.filter { order ->
                order.status == OrderStatus.PAID &&
                        order.timestamp >= start &&
                        order.timestamp <= end
            }

            val dailySales = getDailySalesDataInternal(startDate, endDate, filteredOrders)
            val topSellingItems = getTopSellingItems(filteredOrders)

            val statistics = SalesStatistics(
                totalRevenue = filteredOrders.sumOf { it.totalAmount },
                totalOrders = filteredOrders.size,
                totalItems = filteredOrders.sumOf { order -> order.items.sumOf { it.quantity } },
                averageOrderValue = if (filteredOrders.isNotEmpty()) {
                    filteredOrders.sumOf { it.totalAmount } / filteredOrders.size
                } else 0.0,
                dailySales = dailySales,
                topSellingItems = topSellingItems,
                period = "$startDate to $endDate"
            )

            Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailySalesData(
        startDate: String,
        endDate: String
    ): Result<List<SalesData>> {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = dateFormat.parse(startDate)?.time ?: 0L
            val end = dateFormat.parse(endDate)?.time ?: Long.MAX_VALUE

            val filteredOrders = _orders.value.filter { order ->
                order.status == OrderStatus.PAID &&
                        order.timestamp >= start &&
                        order.timestamp <= end
            }

            val dailySales = getDailySalesDataInternal(startDate, endDate, filteredOrders)
            Result.success(dailySales)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportOrdersToCSV(
        startDate: String?,
        endDate: String?
    ): Result<String> {
        return try {
            val ordersToExport = if (startDate != null && endDate != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val start = dateFormat.parse(startDate)?.time ?: 0L
                val end = dateFormat.parse(endDate)?.time ?: Long.MAX_VALUE

                _orders.value.filter { order ->
                    order.timestamp >= start && order.timestamp <= end
                }
            } else {
                _orders.value
            }

            val csv = StringBuilder()
            csv.appendLine(Order.getCsvHeader())

            ordersToExport.forEach { order ->
                csv.appendLine(order.toCsvRow())
            }

            Result.success(csv.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportSalesStatisticsToCSV(
        startDate: String,
        endDate: String
    ): Result<String> {
        return try {
            val salesDataResult = getDailySalesData(startDate, endDate)
            if (salesDataResult.isSuccess) {
                val salesData = salesDataResult.getOrNull() ?: emptyList()

                val csv = StringBuilder()
                csv.appendLine(SalesData.getCsvHeader())

                salesData.forEach { data ->
                    csv.appendLine(data.toCsvRow())
                }

                Result.success(csv.toString())
            } else {
                Result.failure(salesDataResult.exceptionOrNull() ?: Exception("Failed to get sales data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun searchOrders(
        query: String,
        status: OrderStatus?,
        startDate: String?,
        endDate: String?
    ): Result<List<Order>> {
        return try {
            var filteredOrders = _orders.value

            if (status != null) {
                filteredOrders = filteredOrders.filter { it.status == status }
            }

            if (startDate != null && endDate != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val start = dateFormat.parse(startDate)?.time ?: 0L
                val end = dateFormat.parse(endDate)?.time ?: Long.MAX_VALUE

                filteredOrders = filteredOrders.filter { order ->
                    order.timestamp >= start && order.timestamp <= end
                }
            }

            if (query.isNotBlank()) {
                filteredOrders = filteredOrders.filter { order ->
                    order.id.contains(query, ignoreCase = true) ||
                            order.customerName.contains(query, ignoreCase = true) ||
                            order.items.any { it.menuItem.name.contains(query, ignoreCase = true) }
                }
            }

            Result.success(filteredOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> {
        return _orders.map { orders ->
            orders.filter { it.status == status }
        }
    }

    override suspend fun getOrdersByDate(date: String): Result<List<Order>> {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetDate = dateFormat.parse(date)?.time ?: 0L
            val nextDay = targetDate + 24 * 60 * 60 * 1000 // Add 24 hours

            val filteredOrders = _orders.value.filter { order ->
                order.timestamp >= targetDate && order.timestamp < nextDay
            }

            Result.success(filteredOrders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun createBackup(): Result<String> {
        return try {
            val backup = mapOf(
                "orders" to _orders.value,
                "menuItems" to _menuItems.value,
                "timestamp" to System.currentTimeMillis()
            )

            val backupString = backup.toString()
            Result.success(backupString)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restoreFromBackup(backupData: String): Result<Boolean> {
        return try {
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCache(): Result<Boolean> {
        return try {
            _orders.value = emptyList()
            _menuItems.value = getDefaultMenuItems()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshCache(): Result<Boolean> {
        return try {
            // In production, reload from database
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getDailySalesDataInternal(
        startDate: String,
        endDate: String,
        orders: List<Order>
    ): List<SalesData> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return orders
            .groupBy { order ->
                dateFormat.format(Date(order.timestamp))
            }
            .map { (date, ordersForDate) ->
                SalesData.fromOrders(date, ordersForDate)
            }
            .sortedBy { it.date }
    }

    private fun getTopSellingItems(orders: List<Order>): List<Pair<String, Int>> {
        return orders
            .flatMap { it.items }
            .groupBy { it.menuItem.name }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }
            .toList()
            .sortedByDescending { it.second }
            .take(10)
    }

    private fun getDefaultMenuItems(): List<MenuItem> {
        return listOf(
            MenuItem("1", "Pancit Canton", 25.0, "Noodles", "Stir-fried wheat noodles"),
            MenuItem("2", "Pancit Bihon", 20.0, "Noodles", "Rice vermicelli noodles"),
            MenuItem("3", "Lomi", 30.0, "Noodles", "Thick egg noodles in broth"),
            MenuItem("4", "Mami", 35.0, "Noodles", "Noodle soup with meat"),
            MenuItem("5", "Beef Noodles", 40.0, "Noodles", "Noodles with beef"),
            MenuItem("6", "Chicken Noodles", 35.0, "Noodles", "Noodles with chicken"),
            MenuItem("7", "Pork Noodles", 38.0, "Noodles", "Noodles with pork"),
            MenuItem("8", "Seafood Noodles", 45.0, "Noodles", "Noodles with mixed seafood"),
            MenuItem("9", "Vegetable Noodles", 28.0, "Noodles", "Noodles with vegetables"),
            MenuItem("10", "Special Noodles", 50.0, "Noodles", "House special noodles")
        )
    }

    private fun initializeSampleData() {
        val sampleOrders = listOf(
            Order(
                id = "ORD-SAMPLE-001",
                items = mutableListOf(
                    OrderItem(menuItem = _menuItems.value[0], quantity = 2),
                    OrderItem(menuItem = _menuItems.value[1], quantity = 1)
                ),
                timestamp = System.currentTimeMillis() - 86400000, // Yesterday
                status = OrderStatus.PAID,
                paymentAmount = 70.0,
                customerName = "Sample Customer 1"
            ),
            Order(
                id = "ORD-SAMPLE-002",
                items = mutableListOf(
                    OrderItem(menuItem = _menuItems.value[2], quantity = 1),
                    OrderItem(menuItem = _menuItems.value[3], quantity = 1)
                ),
                timestamp = System.currentTimeMillis() - 43200000, // 12 hours ago
                status = OrderStatus.PAID,
                paymentAmount = 65.0,
                customerName = "Sample Customer 2"
            )
        )

        _orders.value = sampleOrders
    }
}