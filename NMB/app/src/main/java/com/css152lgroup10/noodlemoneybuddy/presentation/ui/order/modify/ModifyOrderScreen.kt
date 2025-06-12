package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.modify

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.css152lgroup10.noodlemoneybuddy.data.model.Order
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.modify.components.SwipeToDeleteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyOrderScreen(
    orders: List<Order>,
    onDelete: (Order) -> Unit,
    onSelectOrder: (Order) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Modify Orders") })
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            items(orders) { order ->
                SwipeToDeleteCard(
                    order = order,
                    onDelete = { onDelete(order) },
                    onClick = { onSelectOrder(order) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
