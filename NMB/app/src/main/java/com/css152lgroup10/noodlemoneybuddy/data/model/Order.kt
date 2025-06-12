package com.css152lgroup10.noodlemoneybuddy.data.model

import java.text.SimpleDateFormat
import java.util.*

data class Order(
    val id: String = generateOrderId(),
    val items: MutableList<OrderItem> = mutableListOf(),
    val timestamp: Long = System.currentTimeMillis(),
    var status: OrderStatus = OrderStatus.PENDING,
    var paymentAmount: Double = 0.0,
    var changeAmount: Double = 0.0,
    val customerName: String = "",
    val notes: String = ""
) {
    val totalAmount: Double
        get() = items.sumOf { it.totalPrice }

    val remainingAmount: Double
        get() = maxOf(0.0, totalAmount - paymentAmount)

    val isFullyPaid: Boolean
        get() = paymentAmount >= totalAmount

    val hasChange: Boolean
        get() = paymentAmount > totalAmount

    fun getFormattedTimestamp(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun getFormattedTotalAmount(): String = "₱%.2f".format(totalAmount)

    fun getFormattedPaymentAmount(): String = "₱%.2f".format(paymentAmount)

    fun getFormattedChangeAmount(): String = "₱%.2f".format(changeAmount)

    fun getFormattedRemainingAmount(): String = "₱%.2f".format(remainingAmount)


    fun addItem(menuItem: MenuItem, quantity: Int) {
        val existingItem = items.find { it.menuItem.id == menuItem.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            items.add(OrderItem(menuItem = menuItem, quantity = quantity))
        }
    }


    fun removeItem(orderItem: OrderItem) {
        items.remove(orderItem)
    }

    fun updateItemQuantity(orderItemId: String, newQuantity: Int) {
        val item = items.find { it.id == orderItemId }
        if (item != null) {
            if (newQuantity <= 0) {
                items.remove(item)
            } else {
                item.quantity = newQuantity
            }
        }
    }

    fun clearItems() {
        items.clear()
    }


    fun processPayment(amount: Double): PaymentResult {
        if (amount <= 0) {
            return PaymentResult.Error("Invalid payment amount")
        }

        paymentAmount += amount

        return when {
            paymentAmount >= totalAmount -> {
                changeAmount = paymentAmount - totalAmount
                status = OrderStatus.PAID
                PaymentResult.Success(changeAmount)
            }
            else -> {
                PaymentResult.PartialPayment(remainingAmount)
            }
        }
    }

    fun cancel() {
        status = OrderStatus.CANCELLED
        clearItems()
    }

    fun isValid(): Boolean {
        return id.isNotBlank() &&
                items.isNotEmpty() &&
                items.all { it.isValid() } &&
                totalAmount > 0.0
    }

    fun getSummary(): String {
        val itemCount = items.size
        val totalQty = items.sumOf { it.quantity }
        return "$itemCount items ($totalQty pieces) - ${getFormattedTotalAmount()}"
    }

    fun toCsvRow(): String {
        val itemsStr = items.joinToString("; ") { "${it.menuItem.name} x${it.quantity}" }
        return "$id,${getFormattedTimestamp()},\"$itemsStr\",${totalAmount},$status"
    }

    companion object {

        private fun generateOrderId(): String {
            val timestamp = System.currentTimeMillis()
            val random = (1000..9999).random()
            return "ORD-$timestamp-$random"
        }

        fun getCsvHeader(): String {
            return "Order ID,Date,Items,Total Amount,Status"
        }
    }
}