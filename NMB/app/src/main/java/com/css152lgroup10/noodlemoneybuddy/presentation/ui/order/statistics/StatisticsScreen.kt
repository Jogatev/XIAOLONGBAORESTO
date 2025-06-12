package com.css152lgroup10.noodlemoneybuddy.presentation.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.statistics.components.SalesChart
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.statistics.components.ExportDialog

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = viewModel()) {
    val stats by viewModel.statistics.collectAsState()
    val showExportDialog by viewModel.showExportDialog.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sales Statistics", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        stats?.let {
            Text("Total Revenue: ${it.getFormattedTotalRevenue()}")
            Text("Total Orders: ${it.totalOrders}")
            Text("Total Items Sold: ${it.totalItems}")
            Text("Average Order Value: ${it.getFormattedAverageOrderValue()}")
            Text("Period: ${it.period}")

            Spacer(modifier = Modifier.height(24.dp))

            SalesChart(data = it.dailySales)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.onExportClick() }) {
            Text("Export Statistics")
        }

        if (showExportDialog) {
            ExportDialog(
                onDismiss = { viewModel.onExportDismiss() },
                onConfirm = { start, end -> viewModel.exportStatistics(start, end) }
            )
        }
    }
}
