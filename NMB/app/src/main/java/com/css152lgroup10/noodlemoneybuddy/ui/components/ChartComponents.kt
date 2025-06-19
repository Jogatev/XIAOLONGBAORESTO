package com.css152lgroup10.noodlemoneybuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.css152lgroup10.noodlemoneybuddy.data.models.SalesDataPoint
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun SalesChart(
    salesData: List<SalesDataPoint>,
    title: String,
    valueFormatter: (Float, ChartValues) -> String = { value, _ -> "₱${value.roundToInt()}" }
) {
    if (salesData.isEmpty()) {
        return
    }

    val entries = salesData.mapIndexed { index, dataPoint ->
        FloatEntry(
            x = index.toFloat(),
            y = dataPoint.amount.toFloat()
        )
    }

    val model = entryModelOf(entries)
    val datesFormatter: (Float, ChartValues) -> String = { index, _ ->
        if (index.toInt() in salesData.indices) {
            SimpleDateFormat("MM/dd", Locale.getDefault())
                .format(salesData[index.toInt()].date)
        } else ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Chart(
                chart = lineChart(),
                model = model,
                startAxis = startAxis(
                    valueFormatter = valueFormatter
                ),
                bottomAxis = bottomAxis(
                    valueFormatter = datesFormatter
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
} 