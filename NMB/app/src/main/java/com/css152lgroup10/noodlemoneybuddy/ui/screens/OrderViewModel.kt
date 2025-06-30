package com.css152lgroup10.noodlemoneybuddy.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.css152lgroup10.noodlemoneybuddy.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.*

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrderRepository.getInstance(application)

    private val _orders = MutableStateFlow<List<OrderWithItems>>(emptyList())
    val orders: StateFlow<List<OrderWithItems>> = _orders.asStateFlow()

    // For repeat order
    private var _pendingOrderItems: List<OrderItem>? = null
    fun setPendingOrderItems(items: List<OrderItem>) {
        _pendingOrderItems = items.map { it.copy(id = UUID.randomUUID().toString(), orderId = "temp") }
    }
    fun consumePendingOrderItems(): List<OrderItem>? {
        val items = _pendingOrderItems
        _pendingOrderItems = null
        return items
    }

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            repository.getAllOrdersWithItemsFlow().collect {
                _orders.value = it
            }
        }
    }

    fun addOrder(order: OrderRecord, items: List<OrderItem>) {
        viewModelScope.launch {
            repository.insertOrderWithItems(order, items)
            loadOrders()
        }
    }

    fun updateOrder(order: OrderRecord) {
        viewModelScope.launch {
            repository.updateOrder(order)
            loadOrders()
        }
    }

    fun deleteOrder(order: OrderRecord) {
        viewModelScope.launch {
            repository.deleteOrder(order)
            loadOrders()
        }
    }

    suspend fun getOrderWithItems(orderId: String): OrderWithItems? {
        return repository.getOrderWithItems(orderId)
    }

    // Expose a StateFlow for a single order with items by orderId
    fun orderWithItemsFlow(orderId: String): StateFlow<OrderWithItems?> =
        orders.map { list -> list.find { it.order.id == orderId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
} 