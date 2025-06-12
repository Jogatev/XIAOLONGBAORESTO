package com.css152lgroup10.noodlemoneybuddy.domain.usecase

import com.css152lgroup10.noodlemoneybuddy.data.model.Order
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository

class ModifyOrderUseCase(
    private val orderRepository: IOrderRepository
) {
    suspend fun execute(order: Order): Result<Order> {
        return orderRepository.updateOrder(order)
    }
}
