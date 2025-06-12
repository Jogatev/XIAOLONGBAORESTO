package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun QuantityDialog(
    visible: Boolean,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enter quantity", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = quantity.toString(),
                        onValueChange = { value ->
                            quantity = value.toIntOrNull()?.coerceAtLeast(1) ?: 1
                        },
                        label = { Text("Quantity") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onConfirm(quantity) }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
