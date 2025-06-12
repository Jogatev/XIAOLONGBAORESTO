package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.modify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OrderListScreen(
    viewModel: ModifyOrderViewModel = hiltViewModel(),
    onOrderSelected: (com.css152lgroup10.noodlemoneybuddy.data.model.Order) -> Unit
) {
    val orders = viewModel.orders.collectAsState().value

    ModifyOrderScreen(
        orders = orders,
        onDelete = { viewModel.deleteOrder(it) },
        onSelectOrder = { onOrderSelected(it) }
    )
}
