package com.css152lgroup10.noodlemoneybuddy.data.model

enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    PAID("Paid"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    fun canBeModified(): Boolean {
        return this == PENDING
    }

    fun canBeCancelled(): Boolean {
        return this == PENDING
    }

    fun isCompleted(): Boolean {
        return this == PAID || this == CANCELLED || this == REFUNDED
    }

    companion object {

        fun fromString(value: String): com.css152lgroup10.noodlemoneybuddy.data.model.OrderStatus {
            return values().find { it.name.equals(value, ignoreCase = true) }
                ?: PENDING
        }
    }
}