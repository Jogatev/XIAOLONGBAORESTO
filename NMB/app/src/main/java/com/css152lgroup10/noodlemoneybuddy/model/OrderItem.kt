package com.css152lgroup10.noodlemoneybuddy.model

data class OrderItem(
    val name: String,
    val price: Double,
    var quantity: Int = 1
) {
    val total: Double
        get() = price * quantity
}
