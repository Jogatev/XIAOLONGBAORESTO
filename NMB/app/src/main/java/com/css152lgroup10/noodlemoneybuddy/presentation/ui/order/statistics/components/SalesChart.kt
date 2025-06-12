package com.css152lgroup10.noodlemoneybuddy.presentation.ui.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.css152lgroup10.noodlemoneybuddy.data.model.SalesData

@Composable
fun SalesChart(data: List<SalesData>) {
    val maxSales = data.maxOfOrNull { it.totalSales } ?: 1.0
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Daily Sales Chart", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)) {
            val barWidth = size.width / (data.size * 2)
            data.forEachIndexed { index, sales ->
                val barHeight = (sales.totalSales / maxSales * size.height).toFloat()
                val x = barWidth * (2 * index + 1)
                drawRect(
                    color = Color(0xFF4CAF50),
                    topLeft = Offset(x, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach {
                Text(text = it.getShortFormattedDate(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
