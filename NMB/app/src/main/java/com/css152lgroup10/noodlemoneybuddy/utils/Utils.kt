package com.css152lgroup10.noodlemoneybuddy.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderRecord
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

fun calculateStatistics(orderRecords: List<OrderRecord>): OrderStatistics {
    if (orderRecords.isEmpty()) {
        return OrderStatistics(0, 0.0, 0.0, "None", 0, 0, 0.0, 0, 0.0, 0, 0.0)
    }

    val totalOrders = orderRecords.size
    val totalRevenue = orderRecords.sumOf { it.totalAmount }
    val averageOrderValue = totalRevenue / totalOrders

    val itemCounts = mutableMapOf<String, Int>()
    orderRecords.forEach { order ->
        order.items.forEach { item ->
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

    val todayOrders = orderRecords.filter { it.timestamp >= startOfToday }
    val thisWeekOrders = orderRecords.filter { it.timestamp >= startOfWeek }
    val thisMonthOrders = orderRecords.filter { it.timestamp >= startOfMonth }

    return OrderStatistics(
        totalOrders = totalOrders,
        totalRevenue = totalRevenue,
        averageOrderValue = averageOrderValue,
        mostPopularItem = mostPopularItemName,
        mostPopularItemCount = mostPopularItemCount,
        todayOrders = todayOrders.size,
        todayRevenue = todayOrders.sumOf { it.totalAmount },
        thisWeekOrders = thisWeekOrders.size,
        thisWeekRevenue = thisWeekOrders.sumOf { it.totalAmount },
        thisMonthOrders = thisMonthOrders.size,
        thisMonthRevenue = thisMonthOrders.sumOf { it.totalAmount }
    )
}

fun processOrdersForVisualization(orders: List<OrderRecord>): List<SalesDataPoint> {
    return orders
        .groupBy { order ->
            val calendar = Calendar.getInstance()
            calendar.time = order.timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }
        .map { (date, ordersForDate) ->
            SalesDataPoint(
                date = date,
                amount = ordersForDate.sumOf { it.totalAmount },
                itemCount = ordersForDate.sumOf { it.items.size }
            )
        }
        .sortedBy { it.date }
}

fun exportToCSV(context: Context, orderRecords: List<OrderRecord>): Boolean {
    return try {
        val fileName = "noodle_buddy_orders_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            writer.append("Order ID,Date,Item Name,Quantity,Unit Price,Total Price,Order Total,Amount Tendered,Change Given\n")
            orderRecords.forEach { order ->
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.timestamp)
                order.items.forEach { item ->
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

fun exportToExcel(context: Context, orderRecords: List<OrderRecord>): Boolean {
    return try {
        val fileName = "noodle_buddy_orders_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        FileWriter(file).use { writer ->
            writer.append("Order ID\tDate\tItem Name\tQuantity\tUnit Price\tTotal Price\tOrder Total\tAmount Tendered\tChange Given\n")
            orderRecords.forEach { order ->
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.timestamp)
                order.items.forEach { item ->
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