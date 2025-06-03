// ui/components/OrderHistoryDialog.kt
package com.css1521group10.noodlemoneybuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.css1521group10.noodlemoneybuddy.model.Order
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryDialog(
    orders: List<Order>,
    onDismiss: () -> Unit
) {
    val decimalFormat = DecimalFormat("#,##0.00")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Order History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No orders yet")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orders.reversed()) { order ->
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Order #${order.id.take(8)}",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "₱${decimalFormat.format(order.total)}",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (order.customerName.isNotEmpty()) {
                                    Text(
                                        text = "Customer: ${order.customerName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = "Payment: ${order.paymentMethod?.name?.replace("_", " ") ?: "N/A"}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                                        .format(Date(order.timestamp)),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Items:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                order.items.forEach { item ->
                                    Text(
                                        text = "• ${item.name} x${item.quantity}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}