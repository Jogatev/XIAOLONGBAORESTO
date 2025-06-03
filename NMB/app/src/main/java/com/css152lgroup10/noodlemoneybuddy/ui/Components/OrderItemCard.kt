
package com.css1521group10.noodlemoneybuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.css1521group10.noodlemoneybuddy.model.OrderItem
import java.text.DecimalFormat

@Composable
fun OrderItemCard(
    item: OrderItem,
    onQuantityChange: (Int) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "₱${DecimalFormat("#,##0.00").format(item.price)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "₱${DecimalFormat("#,##0.00").format(item.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) }
                ) {
                    Text("-", fontSize = 18.sp)
                }

                Text(
                    text = item.quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) }
                ) {
                    Text("+", fontSize = 18.sp)
                }
            }
        }
    }
}