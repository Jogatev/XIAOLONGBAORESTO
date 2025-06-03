
package com.css1521group10.noodlemoneybuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.css1521group10.noodlemoneybuddy.model.Order
import java.text.DecimalFormat

@Composable
fun OrderTotalSection(order: Order) {
    val decimalFormat = DecimalFormat("#,##0.00")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal:")
            Text("₱${decimalFormat.format(order.subtotal)}")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tax (12%):")
            Text("₱${decimalFormat.format(order.tax)}")
        }

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "₱${decimalFormat.format(order.total)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}