package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderWithItems
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRecordsScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    val orders by orderViewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Records") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No order records found.\nStart taking orders to see them here!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(orders, key = { it.order.id }) { orderWithItems ->
                    OrderRecordCard(
                        orderWithItems = orderWithItems,
                        onClick = { navController.navigate("order_detail/${orderWithItems.order.id}") },
                        onDelete = { showDeleteDialog = orderWithItems.order.id }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDeleteDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Order Record?") },
                text = { Text("Are you sure you want to delete this order record?") },
                confirmButton = {
                    TextButton(onClick = {
                        val order = orders.find { it.order.id == showDeleteDialog }
                        if (order != null) orderViewModel.deleteOrder(order.order)
                        showDeleteDialog = null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun OrderRecordCard(
    orderWithItems: OrderWithItems,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val orderRecord = orderWithItems.order
    val items = orderWithItems.items
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Order #${orderRecord.id.take(8)}...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                            .format(orderRecord.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "₱${"%.2f".format(orderRecord.totalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete order record")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${items.size} item(s)",
                style = MaterialTheme.typography.bodyMedium
            )
            val itemsToShow = items.take(2)
            itemsToShow.forEach { item ->
                Text(
                    "• ${item.name} (${item.quantity}x)",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (items.size > 2) {
                Text(
                    "• ... and ${items.size - 2} more items",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 