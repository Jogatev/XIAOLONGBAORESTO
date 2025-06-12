package com.css152lgroup10.noodlemoneybuddy.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.css152lgroup10.noodlemoneybuddy.data.model.Order
import com.css152lgroup10.noodlemoneybuddy.data.model.OrderStatus
import com.css152lgroup10.noodlemoneybuddy.data.model.SalesData
import com.css152lgroup10.noodlemoneybuddy.data.model.SalesStatistics
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class GenerateStatisticsUseCase(
    private val orderRepository: IOrderRepository
) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun execute(): Flow<SalesStatistics> {
        return orderRepository.getAllOrders().map { allOrders ->
            val paidOrders = allOrders.filter { it.status == OrderStatus.PAID }

            val groupedByDate: Map<String, List<Order>> = paidOrders.groupBy {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                formatter.format(Date(it.timestamp))
            }

            val dailySales: List<SalesData> = groupedByDate.map { (date, ordersForDay) ->
                SalesData.fromOrders(date, ordersForDay)
            }.sortedBy { it.date }

            val totalRevenue = dailySales.sumOf { it.totalSales }
            val totalOrders = dailySales.sumOf { it.orderCount }
            val totalItems = dailySales.sumOf { it.itemsSold }
            val averageOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

            val itemFrequency: MutableMap<String, Int> = mutableMapOf()
            paidOrders.forEach { order ->
                order.items.forEach { orderItem ->
                    val name = orderItem.menuItem.name
                    itemFrequency[name] = itemFrequency.getOrDefault(name, 0) + orderItem.quantity
                }
            }

            val topSellingItems = itemFrequency.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key to it.value }

            val period = if (dailySales.isNotEmpty()) {
                val start = dailySales.first().getFormattedDate()
                val end = dailySales.last().getFormattedDate()
                "$start - $end"
            } else {
                "No Data"
            }

            SalesStatistics(
                totalRevenue = totalRevenue,
                totalOrders = totalOrders,
                totalItems = totalItems,
                averageOrderValue = averageOrderValue,
                dailySales = dailySales,
                topSellingItems = topSellingItems,
                period = period
            )
        }
    }
}
