package com.css152lgroup10.noodlemoneybuddy.data.models

import androidx.room.*
import java.util.*

@Entity(
    tableName = "order_items"
)
data class OrderItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val orderId: String, // Foreign key to OrderRecord
    val name: String,
    val quantity: Int,
    val price: Double
) {
    fun getTotalPrice(): Double = quantity * price
}

// MenuItem and OrderStatistics are not Room entities

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double
)

data class OrderStatistics(
    val totalOrders: Int,
    val totalRevenue: Double,
    val averageOrderValue: Double,
    val mostPopularItem: String,
    val mostPopularItemCount: Int,
    val todayOrders: Int,
    val todayRevenue: Double,
    val thisWeekOrders: Int,
    val thisWeekRevenue: Double,
    val thisMonthOrders: Int,
    val thisMonthRevenue: Double
)

@Entity(tableName = "order_records")
data class OrderRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val totalAmount: Double,
    val amountTendered: Double,
    val changeGiven: Double,
    val timestamp: Date
)

// Data class for Room relation
// This is not an entity, just a helper for queries
// Room will auto-populate the items list
class OrderWithItems(
    @Embedded val order: OrderRecord,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItem>
)

data class SalesDataPoint(
    val date: Date,
    val amount: Double,
    val itemCount: Int
) 