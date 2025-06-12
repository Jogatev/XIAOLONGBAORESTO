package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components.MenuItemCard
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components.OrderItemCard
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components.PaymentDialog
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components.QuantityDialog

@Composable
fun CreateOrderScreen(viewModel: CreateOrderViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    QuantityDialog(
        visible = uiState.showQuantityDialog,
        onConfirm = { quantity -> viewModel.addItemToOrder(quantity) },
        onDismiss = { viewModel.dismissQuantityDialog() }
    )

    PaymentDialog(
        visible = uiState.showPaymentDialog,
        totalAmount = uiState.currentOrder.totalAmount,
        onConfirm = { amount -> viewModel.processPayment(amount) },
        onDismiss = { viewModel.dismissPaymentDialog() }
    )

    uiState.menuItems.forEach { item ->
        MenuItemCard(menuItem = item, onClick = { viewModel.selectMenuItem(item) })
    }

    uiState.currentOrder.items.forEach { orderItem ->
        OrderItemCard(orderItem = orderItem, onRemove = { viewModel.removeItem(orderItem) })
    }
}
