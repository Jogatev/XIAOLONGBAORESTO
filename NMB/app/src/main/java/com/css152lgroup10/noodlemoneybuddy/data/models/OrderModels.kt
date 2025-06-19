package com.css152lgroup10.noodlemoneybuddy.data.models

import java.util.*

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int,
    val price: Double
) {
    fun getTotalPrice(): Double = quantity * price
}

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

data class OrderRecord(
    val id: String = UUID.randomUUID().toString(),
    val items: List<OrderItem>,
    val totalAmount: Double,
    val amountTendered: Double,
    val changeGiven: Double,
    val timestamp: Date
)

data class SalesDataPoint(
    val date: Date,
    val amount: Double,
    val itemCount: Int
) 