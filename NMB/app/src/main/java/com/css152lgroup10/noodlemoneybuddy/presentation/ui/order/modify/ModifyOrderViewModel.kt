package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.css152lgroup10.noodlemoneybuddy.data.model.Order
import com.css152lgroup10.noodlemoneybuddy.data.model.OrderStatus
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyOrderViewModel @Inject constructor(
    private val repository: IOrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            repository.getAllOrders()
                .collect { allOrders ->
                    _orders.value = allOrders.filter { it.status != OrderStatus.CANCELLED }
                }
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            repository.deleteOrder(order.id)
            loadOrders()
        }
    }
}