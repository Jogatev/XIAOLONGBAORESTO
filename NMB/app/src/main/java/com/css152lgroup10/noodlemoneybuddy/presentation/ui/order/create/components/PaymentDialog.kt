package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PaymentDialog(
    visible: Boolean,
    totalAmount: Double,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var payment by remember { mutableStateOf("") }

    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
                    Text("Enter payment", style = MaterialTheme.typography.titleMedium)
                    Text("Total: ₱%.2f".format(totalAmount))
                    OutlinedTextField(
                        value = payment,
                        onValueChange = { payment = it },
                        label = { Text("Payment") }
                    )
                    Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                    Button(onClick = {
                        val amount = payment.toDoubleOrNull() ?: 0.0
                        onConfirm(amount)
                    }) {
                        Text("Pay")
                    }
                }
            }
        }
    }
}
