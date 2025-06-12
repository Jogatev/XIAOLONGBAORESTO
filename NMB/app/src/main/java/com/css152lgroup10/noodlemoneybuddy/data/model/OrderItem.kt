package com.css152lgroup10.noodlemoneybuddy.data.model

data class OrderItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val menuItem: MenuItem,
    var quantity: Int,
    val unitPrice: Double = menuItem.price,
    val notes: String = ""
) {
    val totalPrice: Double
        get() = unitPrice * quantity

    fun getFormattedTotalPrice(): String = "₱%.2f".format(totalPrice)

    fun getFormattedUnitPrice(): String = "₱%.2f".format(unitPrice)

    fun getQuantityText(): String = "x$quantity"

    fun isValid(): Boolean {
        return quantity > 0 &&
                unitPrice > 0.0 &&
                menuItem.isValid()
    }

    fun withQuantity(newQuantity: Int): OrderItem {
        return copy(quantity = newQuantity)
    }
}