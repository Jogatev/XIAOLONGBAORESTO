package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.css152lgroup10.noodlemoneybuddy.data.model.OrderItem

@Composable
fun OrderItemCard(orderItem: OrderItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onRemove() }
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(orderItem.menuItem.name, style = MaterialTheme.typography.titleMedium)
                Text("x${orderItem.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            Text("₱%.2f".format(orderItem.totalPrice), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
