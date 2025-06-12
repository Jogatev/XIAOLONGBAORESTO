package com.css152lgroup10.noodlemoneybuddy.data.model

import java.text.SimpleDateFormat
import java.util.*

data class SalesData(
    val date: String,
    val totalSales: Double,
    val orderCount: Int,
    val itemsSold: Int = 0,
    val averageOrderValue: Double = if (orderCount > 0) totalSales / orderCount else 0.0
) {
    fun getFormattedTotalSales(): String = "₱%.2f".format(totalSales)

    fun getFormattedAverageOrderValue(): String = "₱%.2f".format(averageOrderValue)

    fun getFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: Date())
        } catch (e: Exception) {
            date
        }
    }

    fun getShortFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: Date())
        } catch (e: Exception) {
            date
        }
    }

    fun toCsvRow(): String {
        return "$date,$totalSales,$orderCount,$itemsSold,$averageOrderValue"
    }

    companion object {

        fun getCsvHeader(): String {
            return "Date,Total Sales,Order Count,Items Sold,Average Order Value"
        }

        fun fromOrders(date: String, orders: List<Order>): SalesData {
            val validOrders = orders.filter { it.status == OrderStatus.PAID }
            return SalesData(
                date = date,
                totalSales = validOrders.sumOf { it.totalAmount },
                orderCount = validOrders.size,
                itemsSold = validOrders.sumOf { order -> order.items.sumOf { it.quantity } }
            )
        }
    }
}

data class SalesStatistics(
    val totalRevenue: Double,
    val totalOrders: Int,
    val totalItems: Int,
    val averageOrderValue: Double,
    val dailySales: List<SalesData>,
    val topSellingItems: List<Pair<String, Int>>,
    val period: String
) {
    fun getFormattedTotalRevenue(): String = "₱%.2f".format(totalRevenue)

    fun getFormattedAverageOrderValue(): String = "₱%.2f".format(averageOrderValue)

    fun getGrowthRate(previousPeriodRevenue: Double): Double {
        return if (previousPeriodRevenue > 0) {
            ((totalRevenue - previousPeriodRevenue) / previousPeriodRevenue) * 100
        } else {
            0.0
        }
    }

    fun getFormattedGrowthRate(previousPeriodRevenue: Double): String {
        val rate = getGrowthRate(previousPeriodRevenue)
        val sign = if (rate >= 0) "+" else ""
        return "$sign%.1f%%".format(rate)
    }
}