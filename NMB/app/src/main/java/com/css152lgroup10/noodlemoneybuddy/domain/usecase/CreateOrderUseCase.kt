package com.css152lgroup10.noodlemoneybuddy.domain.usecase


import com.css152lgroup10.noodlemoneybuddy.data.model.*
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {

    suspend fun createNewOrder(customerName: String = ""): Result<Order> {
        val newOrder = Order(customerName = customerName)
        return orderRepository.createOrder(newOrder)
    }

    suspend fun getAvailableMenuItems(): List<MenuItem> {
        return orderRepository.getMenuItems().filter { it.isAvailable }
    }

    suspend fun addItemToOrder(
        orderId: String,
        menuItem: MenuItem,
        quantity: Int
    ): Result<Order> {
        if (quantity <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be greater than 0"))
        }

        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        order.addItem(menuItem, quantity)

        return orderRepository.updateOrder(order)
    }

    suspend fun removeItemFromOrder(
        orderId: String,
        orderItem: OrderItem
    ): Result<Order> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        order.removeItem(orderItem)

        return orderRepository.updateOrder(order)
    }

    suspend fun updateItemQuantity(
        orderId: String,
        orderItemId: String,
        newQuantity: Int
    ): Result<Order> {
        if (newQuantity < 0) {
            return Result.failure(IllegalArgumentException("Quantity cannot be negative"))
        }

        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        order.updateItemQuantity(orderItemId, newQuantity)

        return orderRepository.updateOrder(order)
    }

    suspend fun calculateOrderTotal(orderId: String): Result<Double> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        return Result.success(order.totalAmount)
    }

    suspend fun cancelOrder(orderId: String): Result<Boolean> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        order.cancel()

        return orderRepository.updateOrder(order).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun getOrderDetails(orderId: String): Result<Order> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        return Result.success(order)
    }

    suspend fun validateOrder(orderId: String): Result<OrderValidation> {
        val order = orderRepository.getOrderById(orderId)
            ?: return Result.failure(IllegalArgumentException("Order not found"))

        val validation = OrderValidation(
            isValid = order.isValid(),
            errors = mutableListOf<String>().apply {
                if (order.items.isEmpty()) add("Order must have at least one item")
                if (order.totalAmount <= 0) add("Order total must be greater than 0")
                if (order.items.any { !it.isValid() }) add("Some order items are invalid")
            }
        )

        return Result.success(validation)
    }
}

data class OrderValidation(
    val isValid: Boolean,
    val errors: List<String>
) {
    fun getErrorMessage(): String = errors.joinToString("\n")
}