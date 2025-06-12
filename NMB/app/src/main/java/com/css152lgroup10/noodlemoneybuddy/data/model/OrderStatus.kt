package com.css152lgroup10.noodlemoneybuddy.data.model

/**
 * Enum representing different states of an order
 * Used for tracking order lifecycle and payment status
 */
enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    PAID("Paid"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    /**
     * Check if order can be modified
     */
    fun canBeModified(): Boolean {
        return this == PENDING
    }

    /**
     * Check if order can be cancelled
     */
    fun canBeCancelled(): Boolean {
        return this == PENDING
    }

    /**
     * Check if order is completed
     */
    fun isCompleted(): Boolean {
        return this == PAID || this == CANCELLED || this == REFUNDED
    }

    companion object {
        /**
         * Get status from string (for database storage)
         */
        fun fromString(value: String): com.css152lgroup10.noodlemoneybuddy.data.model.OrderStatus {
            return values().find { it.name.equals(value, ignoreCase = true) }
                ?: PENDING
        }
    }
}