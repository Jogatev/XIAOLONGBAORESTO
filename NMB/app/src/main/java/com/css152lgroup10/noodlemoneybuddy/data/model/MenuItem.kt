package com.css152lgroup10.noodlemoneybuddy.data.model

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String = "Noodles",
    val description: String = "",
    val isAvailable: Boolean = true
) {
    fun getFormattedPrice(): String = "₱%.2f".format(price)

    fun isValid(): Boolean {
        return id.isNotBlank() &&
                name.isNotBlank() &&
                price > 0.0
    }}