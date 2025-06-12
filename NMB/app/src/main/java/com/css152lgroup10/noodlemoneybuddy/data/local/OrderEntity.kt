package com.css152lgroup10.noodlemoneybuddy.data.local

import androidx.room.*
import com.css152lgroup10.noodlemoneybuddy.data.model.Order
import com.css152lgroup10.noodlemoneybuddy.data.model.OrderStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val itemsJson: String,
    val totalAmount: Double,
    val status: String,
    val timestamp: Long
) {
    fun toOrder(): Order {
        val listType = object : TypeToken<MutableList<com.css152lgroup10.noodlemoneybuddy.data.model.OrderItem>>() {}.type
        val items = Gson().fromJson<MutableList<com.css152lgroup10.noodlemoneybuddy.data.model.OrderItem>>(itemsJson, listType)
        return Order(
            id = id,
            items = items,
            timestamp = timestamp,
            status = OrderStatus.valueOf(status),
            paymentAmount = 0.0,
            changeAmount = 0.0,
            customerName = customerName,
            notes = ""
        )
    }

    companion object {
        fun fromOrder(order: Order): OrderEntity {
            val json = Gson().toJson(order.items)
            return OrderEntity(order.id, order.customerName, json, order.totalAmount, order.status.name, order.timestamp)
        }
    }
}