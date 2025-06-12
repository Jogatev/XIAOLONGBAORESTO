package com.css152lgroup10.noodlemoneybuddy.data.model

sealed class PaymentResult {

    data class Success(val change: Double) : PaymentResult() {
        fun getFormattedChange(): String = "₱%.2f".format(change)

        fun hasChange(): Boolean = change > 0.0
    }


    data class PartialPayment(val remainingAmount: Double) : PaymentResult() {
        fun getFormattedRemainingAmount(): String = "₱%.2f".format(remainingAmount)
    }


    data class Error(val message: String) : PaymentResult()

    object Cancelled : PaymentResult()

    fun isSuccess(): Boolean = this is Success

    fun needsMorePayment(): Boolean = this is PartialPayment

    fun isError(): Boolean = this is Error

    fun getMessage(): String {
        return when (this) {
            is Success -> if (hasChange()) {
                "Payment successful! Change: ${getFormattedChange()}"
            } else {
                "Payment successful!"
            }
            is PartialPayment -> "Partial payment received. Remaining: ${getFormattedRemainingAmount()}"
            is Error -> "Payment error: $message"
            is Cancelled -> "Payment cancelled"
        }
    }
}