package com.css152lgroup10.noodlemoneybuddy.domain.usecase

import com.css152lgroup10.noodlemoneybuddy.data.model.*
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {
    suspend fun initiatePayment(orderId: String): Result<PaymentSession> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        if (order.items.isEmpty()) {
            return Result.failure(IllegalArgumentException("Cannot process payment for empty order"))
        }

        if (order.status != OrderStatus.PENDING) {
            return Result.failure(IllegalArgumentException("Order is not in pending status"))
        }

        val paymentSession = PaymentSession(
            orderId = orderId,
            requiredAmount = order.totalAmount,
            currentPayment = order.paymentAmount,
            remainingAmount = order.remainingAmount
        )

        return Result.success(paymentSession)
    }

    suspend fun processPayment(
        orderId: String,
        paymentAmount: Double
    ): Result<PaymentResult> {
        if (paymentAmount <= 0) {
            return Result.success(PaymentResult.Error("Payment amount must be greater than 0"))
        }

        val order = orderRepository.getOrderById(orderId)
            ?: return Result.success(PaymentResult.Error("Order not found"))

        val paymentResult = order.processPayment(paymentAmount)

        orderRepository.updateOrder(order).fold(
            onSuccess = { /* Order updated successfully */ },
            onFailure = {
                return Result.success(PaymentResult.Error("Failed to save payment: ${it.message}"))
            }
        )

        return Result.success(paymentResult)
    }

    suspend fun cancelPayment(orderId: String): Result<Boolean> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        return Result.success(true)
    }

    suspend fun getPaymentSession(orderId: String): Result<PaymentSession> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        val paymentSession = PaymentSession(
            orderId = orderId,
            requiredAmount = order.totalAmount,
            currentPayment = order.paymentAmount,
            remainingAmount = order.remainingAmount
        )

        return Result.success(paymentSession)
    }

    suspend fun processPartialPayment(
        orderId: String,
        paymentAmount: Double
    ): Result<PartialPaymentResult> {
        val paymentResult = processPayment(orderId, paymentAmount)

        return paymentResult.fold(
            onSuccess = { result ->
                when (result) {
                    is PaymentResult.Success -> {
                        Result.success(PartialPaymentResult.Completed(result.change))
                    }
                    is PaymentResult.PartialPayment -> {
                        Result.success(PartialPaymentResult.ContinuePayment(result.remainingAmount))
                    }
                    is PaymentResult.Error -> {
                        Result.success(PartialPaymentResult.Error(result.errorMessage))
                    }
                    is PaymentResult.Cancelled -> {
                        Result.success(PartialPaymentResult.Error("Payment was cancelled"))
                    }
                }
            },
            onFailure = {
                Result.success(PartialPaymentResult.Error(it.message ?: "Unknown error"))
            }
        )
    }

    suspend fun calculateChange(
        requiredAmount: Double,
        paymentAmount: Double
    ): Double {
        return if (paymentAmount > requiredAmount) {
            paymentAmount - requiredAmount
        } else {
            0.0
        }
    }

    suspend fun calculateRemainingAmount(
        requiredAmount: Double,
        currentPayment: Double
    ): Double {
        return maxOf(0.0, requiredAmount - currentPayment)
    }

    suspend fun validatePaymentAmount(
        paymentAmount: Double,
        requiredAmount: Double
    ): Result<PaymentValidation> {
        val validation = PaymentValidation(
            isValid = paymentAmount > 0,
            isPartialPayment = paymentAmount > 0 && paymentAmount < requiredAmount,
            isOverpayment = paymentAmount > requiredAmount,
            changeAmount = calculateChange(requiredAmount, paymentAmount),
            errors = mutableListOf<String>().apply {
                if (paymentAmount <= 0) add("Payment amount must be greater than 0")
                if (paymentAmount > requiredAmount * 10) add("Payment amount seems unusually high")
            }
        )

        return Result.success(validation)
    }

    suspend fun getPaymentHistory(orderId: String): Result<List<PaymentRecord>> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        val records = if (order.paymentAmount > 0) {
            listOf(
                PaymentRecord(
                    amount = order.paymentAmount,
                    timestamp = order.timestamp,
                    method = "Cash" // Default payment method
                )
            )
        } else {
            emptyList()
        }

        return Result.success(records)
    }
}

data class PaymentSession(
    val orderId: String,
    val requiredAmount: Double,
    val currentPayment: Double,
    val remainingAmount: Double
) {
    fun getFormattedRequiredAmount(): String = "₱%.2f".format(requiredAmount)
    fun getFormattedCurrentPayment(): String = "₱%.2f".format(currentPayment)
    fun getFormattedRemainingAmount(): String = "₱%.2f".format(remainingAmount)

    fun isFullyPaid(): Boolean = currentPayment >= requiredAmount
    fun hasPartialPayment(): Boolean = currentPayment > 0 && currentPayment < requiredAmount
}

sealed class PartialPaymentResult {
    data class Completed(val change: Double) : PartialPaymentResult()
    data class ContinuePayment(val remainingAmount: Double) : PartialPaymentResult()
    data class Error(val message: String) : PartialPaymentResult()
}

data class PaymentValidation(
    val isValid: Boolean,
    val isPartialPayment: Boolean,
    val isOverpayment: Boolean,
    val changeAmount: Double,
    val errors: List<String>
) {
    fun getErrorMessage(): String = errors.joinToString("\n")
}

data class PaymentRecord(
    val amount: Double,
    val timestamp: Long,
    val method: String
) {
    fun getFormattedAmount(): String = "₱%.2f".format(amount)
    fun getFormattedTimestamp(): String {
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(java.util.Date(timestamp))
    }
}