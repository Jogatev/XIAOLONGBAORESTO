package com.css152lgroup10.noodlemoneybuddy.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderWithItems
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderStatistics
import com.css152lgroup10.noodlemoneybuddy.data.models.SalesDataPoint
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ClickDebouncer(private val delayMillis: Long = 500L) {
    private var lastClickTime = 0L

    fun processClick(onClick: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > delayMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

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
    val thisMonthRevenue: Double,
    val mostPopularItemToday: String,
    val mostPopularItemTodayCount: Int,
    val mostPopularItemThisWeek: String,
    val mostPopularItemThisWeekCount: Int,
    val mostPopularItemThisMonth: String,
    val mostPopularItemThisMonthCount: Int
)

fun calculateStatistics(orderWithItemsList: List<OrderWithItems>): OrderStatistics {
    if (orderWithItemsList.isEmpty()) {
        return OrderStatistics(0, 0.0, 0.0, "None", 0, 0, 0.0, 0, 0.0, 0, 0.0, "None", 0, "None", 0, "None", 0)
    }

    val totalOrders = orderWithItemsList.size
    val totalRevenue = orderWithItemsList.sumOf { it.order.totalAmount }
    val averageOrderValue = totalRevenue / totalOrders

    val itemCounts = mutableMapOf<String, Int>()
    orderWithItemsList.forEach { orderWithItems ->
        orderWithItems.items.forEach { item ->
            itemCounts[item.name] = itemCounts.getOrDefault(item.name, 0) + item.quantity
        }
    }
    val mostPopularItem = itemCounts.maxByOrNull { it.value }
    val mostPopularItemName = mostPopularItem?.key ?: "None"
    val mostPopularItemCount = mostPopularItem?.value ?: 0

    val calendar = Calendar.getInstance()
    val today = calendar.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfToday = calendar.time

    calendar.add(Calendar.DAY_OF_YEAR, -7)
    val startOfWeek = calendar.time

    calendar.time = today
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfMonth = calendar.time

    val todayOrders = orderWithItemsList.filter { it.order.timestamp >= startOfToday }
    val thisWeekOrders = orderWithItemsList.filter { it.order.timestamp >= startOfWeek }
    val thisMonthOrders = orderWithItemsList.filter { it.order.timestamp >= startOfMonth }

    fun mostPopularItemFor(orders: List<OrderWithItems>): Pair<String, Int> {
        val itemCounts = mutableMapOf<String, Int>()
        orders.forEach { orderWithItems ->
            orderWithItems.items.forEach { item ->
                itemCounts[item.name] = itemCounts.getOrDefault(item.name, 0) + item.quantity
            }
        }
        val mostPopular = itemCounts.maxByOrNull { it.value }
        return Pair(mostPopular?.key ?: "None", mostPopular?.value ?: 0)
    }
    val (mostPopularItemToday, mostPopularItemTodayCount) = mostPopularItemFor(todayOrders)
    val (mostPopularItemThisWeek, mostPopularItemThisWeekCount) = mostPopularItemFor(thisWeekOrders)
    val (mostPopularItemThisMonth, mostPopularItemThisMonthCount) = mostPopularItemFor(thisMonthOrders)

    return OrderStatistics(
        totalOrders = totalOrders,
        totalRevenue = totalRevenue,
        averageOrderValue = averageOrderValue,
        mostPopularItem = mostPopularItemName,
        mostPopularItemCount = mostPopularItemCount,
        todayOrders = todayOrders.size,
        todayRevenue = todayOrders.sumOf { it.order.totalAmount },
        thisWeekOrders = thisWeekOrders.size,
        thisWeekRevenue = thisWeekOrders.sumOf { it.order.totalAmount },
        thisMonthOrders = thisMonthOrders.size,
        thisMonthRevenue = thisMonthOrders.sumOf { it.order.totalAmount },
        mostPopularItemToday = mostPopularItemToday,
        mostPopularItemTodayCount = mostPopularItemTodayCount,
        mostPopularItemThisWeek = mostPopularItemThisWeek,
        mostPopularItemThisWeekCount = mostPopularItemThisWeekCount,
        mostPopularItemThisMonth = mostPopularItemThisMonth,
        mostPopularItemThisMonthCount = mostPopularItemThisMonthCount
    )
}

fun processOrdersForVisualization(orderWithItemsList: List<OrderWithItems>): List<SalesDataPoint> {
    return orderWithItemsList
        .groupBy { orderWithItems ->
            val calendar = Calendar.getInstance()
            calendar.time = orderWithItems.order.timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }
        .map { (date, ordersForDate) ->
            SalesDataPoint(
                date = date,
                amount = ordersForDate.sumOf { it.order.totalAmount },
                itemCount = ordersForDate.sumOf { it.items.size }
            )
        }
        .sortedBy { it.date }
}

fun exportToCSV(context: Context, orderWithItemsList: List<OrderWithItems>): Boolean {
    return try {
        val fileName = "noodle_buddy_orders_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            writer.append("Order ID,Date,Item Name,Quantity,Unit Price,Total Price,Order Total,Amount Tendered,Change Given\n")
            orderWithItemsList.forEach { orderWithItems ->
                val order = orderWithItems.order
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.timestamp)
                orderWithItems.items.forEach { item ->
                    writer.append("${order.id},")
                    writer.append("$dateString,")
                    writer.append("\"${item.name}\",")
                    writer.append("${item.quantity},")
                    writer.append("${item.price},")
                    writer.append("${item.getTotalPrice()},")
                    writer.append("${order.totalAmount},")
                    writer.append("${order.amountTendered},")
                    writer.append("${order.changeGiven}\n")
                }
            }
        }
        shareFile(context, file, "text/csv")
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun exportToExcel(context: Context, orderWithItemsList: List<OrderWithItems>): Boolean {
    return try {
        val fileName = "noodle_buddy_orders_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            writer.append("Order ID\tDate\tItem Name\tQuantity\tUnit Price\tTotal Price\tOrder Total\tAmount Tendered\tChange Given\n")
            orderWithItemsList.forEach { orderWithItems ->
                val order = orderWithItems.order
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.timestamp)
                orderWithItems.items.forEach { item ->
                    writer.append("${order.id}\t")
                    writer.append("$dateString\t")
                    writer.append("${item.name}\t")
                    writer.append("${item.quantity}\t")
                    writer.append("${item.price}\t")
                    writer.append("${item.getTotalPrice()}\t")
                    writer.append("${order.totalAmount}\t")
                    writer.append("${order.amountTendered}\t")
                    writer.append("${order.changeGiven}\n")
                }
            }
        }
        shareFile(context, file, "application/vnd.ms-excel")
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun shareFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share Export File"))
} 