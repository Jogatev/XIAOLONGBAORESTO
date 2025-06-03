
package com.css1521group10.noodlemoneybuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.css1521group10.noodlemoneybuddy.model.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDialog(
    order: Order,
    onDismiss: () -> Unit,
    onPaymentComplete: (Order) -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var amountPaid by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    val decimalFormat = DecimalFormat("#,##0.00")
    val amountPaidValue = amountPaid.toDoubleOrNull() ?: 0.0
    val change = if (amountPaidValue >= order.total) amountPaidValue - order.total else 0.0
    val isPaymentValid = selectedPaymentMethod != null &&
            (selectedPaymentMethod == PaymentMethod.CASH && amountPaidValue >= order.total ||
                    selectedPaymentMethod != PaymentMethod.CASH)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Process Payment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Order Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₱${decimalFormat.format(order.total)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Payment Method",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PaymentMethod.values()) { method ->
                        Card(
                            onClick = { selectedPaymentMethod = method },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPaymentMethod == method)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == method,
                                    onClick = { selectedPaymentMethod = method }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when(method) {
                                        PaymentMethod.CASH -> "Cash"
                                        PaymentMethod.CARD -> "Card"
                                        PaymentMethod.DIGITAL_WALLET -> "Digital Wallet"
                                        PaymentMethod.BANK_TRANSFER -> "Bank Transfer"
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedPaymentMethod == PaymentMethod.CASH) {
                    OutlinedTextField(
                        value = amountPaid,
                        onValueChange = { amountPaid = it },
                        label = { Text("Amount Paid") },
                        prefix = { Text("₱") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (amountPaidValue > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (change >= 0)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                if (change >= 0) {
                                    Text("Change: ₱${decimalFormat.format(change)}")
                                } else {
                                    Text(
                                        text = "Insufficient amount",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showConfirmation = true },
                enabled = isPaymentValid
            ) {
                Text("Complete Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm Payment") },
            text = {
                Column {
                    Text("Are you sure you want to complete this payment?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total: ₱${decimalFormat.format(order.total)}")
                    Text("Payment Method: ${selectedPaymentMethod?.name?.replace("_", " ")}")
                    if (selectedPaymentMethod == PaymentMethod.CASH) {
                        Text("Amount Paid: ₱${decimalFormat.format(amountPaidValue)}")
                        Text("Change: ₱${decimalFormat.format(change)}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val completedOrder = order.copy(
                            paymentMethod = selectedPaymentMethod,
                            customerName = customerName,
                            status = OrderStatus.COMPLETED
                        )
                        onPaymentComplete(completedOrder)
                        showConfirmation = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Back")
                }
            }
        )
    }
}