package com.css1521group10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.css1521group10.noodlemoneybuddy.model.*
import com.css1521group10.noodlemoneybuddy.ui.components.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POSSystemScreen() {
    var currentOrder by remember { mutableStateOf(Order(id = UUID.randomUUID().toString())) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showOrderHistory by remember { mutableStateOf(false) }
    val orderHistory = remember { mutableStateListOf<Order>() }
//sample lmao
    val products = remember {
        listOf(
            Product("1", "Beef Noodles", 120.0, "Noodles"),
            Product("2", "Chicken Noodles", 100.0, "Noodles"),
            Product("3", "Pork Noodles", 110.0, "Noodles"),
            Product("4", "Vegetable Noodles", 90.0, "Noodles"),
            Product("5", "Soft Drinks", 25.0, "Beverages"),
            Product("6", "Hot Tea", 20.0, "Beverages"),
            Product("7", "Iced Coffee", 35.0, "Beverages"),
            Product("8", "Spring Rolls", 60.0, "Appetizers")
        )
    }

    val categories = products.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf("All") }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Menu",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedCategory = "All" },
                            label = { Text("All") },
                            selected = selectedCategory == "All"
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            selected = selectedCategory == category
                        )
                    }
                }

                val filteredProducts = if (selectedCategory == "All") {
                    products
                } else {
                    products.filter { it.category == selectedCategory }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts.chunked(2)) { productPair ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            productPair.forEach { product ->
                                ProductCard(
                                    product = product,
                                    onAddToOrder = {
                                        val existingItem = currentOrder.items.find { it.id == product.id }
                                        if (existingItem != null) {
                                            existingItem.quantity++
                                        } else {
                                            currentOrder.items.add(
                                                OrderItem(
                                                    id = product.id,
                                                    name = product.name,
                                                    price = product.price,
                                                    category = product.category
                                                )
                                            )
                                        }
                                        currentOrder = currentOrder.copy()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (productPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Order",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { showOrderHistory = true }
                    ) {
                        Text("History")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentOrder.items) { item ->
                        OrderItemCard(
                            item = item,
                            onQuantityChange = { newQuantity ->
                                if (newQuantity <= 0) {
                                    currentOrder.items.remove(item)
                                } else {
                                    item.quantity = newQuantity
                                }
                                currentOrder = currentOrder.copy()
                            }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                OrderTotalSection(order = currentOrder)

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showPaymentDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentOrder.items.isNotEmpty()
                    ) {
                        Text("Process Payment")
                    }

                    OutlinedButton(
                        onClick = {
                            currentOrder = Order(id = UUID.randomUUID().toString())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = currentOrder.items.isNotEmpty()
                    ) {
                        Text("Clear Order")
                    }
                }
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            order = currentOrder,
            onDismiss = { showPaymentDialog = false },
            onPaymentComplete = { order ->
                orderHistory.add(order)
                currentOrder = Order(id = UUID.randomUUID().toString())
                showPaymentDialog = false
            }
        )
    }

    if (showOrderHistory) {
        OrderHistoryDialog(
            orders = orderHistory,
            onDismiss = { showOrderHistory = false }
        )
    }
}