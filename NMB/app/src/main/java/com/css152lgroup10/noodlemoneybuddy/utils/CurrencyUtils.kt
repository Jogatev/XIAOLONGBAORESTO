package com.css152lgroup10.noodlemoneybuddy.utils

object CurrencyUtils {
    fun formatCurrency(value: Double): String {
        return "${Constants.CURRENCY_SYMBOL}%.2f".format(value)
    }
}
