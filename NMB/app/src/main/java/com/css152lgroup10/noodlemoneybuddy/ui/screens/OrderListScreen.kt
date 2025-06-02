package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.css152lgroup10.noodlemoneybuddy.model.OrderItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen() {
    val orders = remember { mutableStateListOf<OrderItem>() }

    val menuItems = listOf(
        OrderItem("Ramen", 8.5),
        OrderItem("Gyoza", 3.0),
        OrderItem("Tempura", 4.5),
        OrderItem("Iced Tea", 2.0),
        OrderItem("Soda", 1.5)
    )

    var selectedItem by remember { mutableStateOf<OrderItem?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var quantityInput by remember { mutableStateOf("1") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Items", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(modifier = Modifier.height(200.dp)) {
            items(menuItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.name} - ₱%.2f".format(item.price))
                    Button(onClick = {
                        selectedItem = item
                        quantityInput = "1"
                        showDialog = true
                    }) {
                        Text("Add")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        Text("Current Order", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(orders, key = { it.name }) { item ->
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                            orders.remove(item)
                            true
                        } else false
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                val totalAmount = orders.sumOf { it.total }

                Text(
                    text = "Total Amount: ₱%.2f".format(totalAmount),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
                SwipeToDismiss(
                    state = dismissState,
                    background = {
                        val color = when (dismissState.dismissDirection) {
                            DismissDirection.StartToEnd, DismissDirection.EndToStart -> Color.Red
                            null -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(20.dp)
                        ) {
                            Text("Removing...", color = Color.White)
                        }
                    },
                    dismissContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.name}")
                            Text("₱%.2f".format(item.total))
                        }
                    }
                )
            }
        }
    }

    if (showDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Quantity") },
            text = {
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = { quantityInput = it },
                    label = { Text("Quantity") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val qty = quantityInput.toIntOrNull() ?: 1
                    if (qty > 0) {
                        val existing = orders.find { it.name == selectedItem!!.name }
                        if (existing != null) {
                            existing.quantity += qty
                        } else {
                            orders.add(selectedItem!!.copy(quantity = qty))
                        }
                        showDialog = false
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
