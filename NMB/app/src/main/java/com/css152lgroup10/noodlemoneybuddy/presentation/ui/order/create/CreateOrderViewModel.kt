package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.css152lgroup10.noodlemoneybuddy.data.model.MenuItem
import com.css152lgroup10.noodlemoneybuddy.data.model.Order
import com.css152lgroup10.noodlemoneybuddy.data.model.OrderItem
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateOrderUiState(
    val menuItems: List<MenuItem> = emptyList(),
    val currentOrder: Order = Order(),
    val selectedMenuItem: MenuItem? = null,
    val showQuantityDialog: Boolean = false,
    val showPaymentDialog: Boolean = false
)

class CreateOrderViewModel(
    private val repository: IOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateOrderUiState())
    val uiState: StateFlow<CreateOrderUiState> = _uiState

    init {
        loadMenuItems()
    }

    private fun loadMenuItems() {
        viewModelScope.launch {
            val items = repository.getMenuItems()
            _uiState.value = _uiState.value.copy(menuItems = items)
        }
    }

    fun selectMenuItem(item: MenuItem) {
        _uiState.value = _uiState.value.copy(selectedMenuItem = item, showQuantityDialog = true)
    }

    fun addItemToOrder(quantity: Int) {
        val item = _uiState.value.selectedMenuItem ?: return
        val updatedOrder = _uiState.value.currentOrder
        updatedOrder.addItem(item, quantity)
        _uiState.value = _uiState.value.copy(
            currentOrder = updatedOrder,
            showQuantityDialog = false,
            selectedMenuItem = null
        )
    }

    fun removeItem(orderItem: OrderItem) {
        val updatedOrder = _uiState.value.currentOrder
        updatedOrder.removeItem(orderItem)
        _uiState.value = _uiState.value.copy(currentOrder = updatedOrder)
    }

    fun showPaymentDialog() {
        _uiState.value = _uiState.value.copy(showPaymentDialog = true)
    }

    fun dismissQuantityDialog() {
        _uiState.value = _uiState.value.copy(showQuantityDialog = false)
    }

    fun dismissPaymentDialog() {
        _uiState.value = _uiState.value.copy(showPaymentDialog = false)
    }

    fun processPayment(amount: Double) {
        val updatedOrder = _uiState.value.currentOrder
        updatedOrder.processPayment(amount)
        viewModelScope.launch {
            repository.saveOrder(updatedOrder)
        }
        _uiState.value = _uiState.value.copy(
            currentOrder = Order(),
            showPaymentDialog = false
        )
    }
}
