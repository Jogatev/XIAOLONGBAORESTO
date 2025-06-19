package com.css152lgroup10.noodlemoneybuddy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderRecord
import com.css152lgroup10.noodlemoneybuddy.data.models.SalesDataPoint
import com.css152lgroup10.noodlemoneybuddy.ui.components.SalesChart
import com.css152lgroup10.noodlemoneybuddy.ui.components.StatisticRow
import com.css152lgroup10.noodlemoneybuddy.ui.components.StatisticsCard
import com.css152lgroup10.noodlemoneybuddy.utils.calculateStatistics
import com.css152lgroup10.noodlemoneybuddy.utils.exportToCSV
import com.css152lgroup10.noodlemoneybuddy.utils.exportToExcel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    orderRecords: List<OrderRecord>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statistics = remember(orderRecords) { calculateStatistics(orderRecords) }
    var showExportMenu by remember { mutableStateOf(false) }
    
    // Process sales data for visualization
    val salesData = remember(orderRecords) { processOrdersForVisualization(orderRecords) }
    
    // Calculate daily averages for the last 7 days
    val last7DaysData = remember(orderRecords) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.time
        orderRecords
            .filter { it.timestamp >= startDate }
            .let { processOrdersForVisualization(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showExportMenu = true }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export Data")
                    }
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export to CSV") },
                            onClick = {
                                showExportMenu = false
                                val success = exportToCSV(context, orderRecords)
                                Toast.makeText(
                                    context,
                                    if (success) "CSV exported successfully!" else "Export failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Share, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export to Excel") },
                            onClick = {
                                showExportMenu = false
                                val success = exportToExcel(context, orderRecords)
                                Toast.makeText(
                                    context,
                                    if (success) "Excel file exported successfully!" else "Export failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Sales Visualization Section
            if (orderRecords.isNotEmpty()) {
                Text(
                    "Sales Visualization",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Last 7 Days Sales Chart
                if (last7DaysData.isNotEmpty()) {
                    SalesChart(
                        salesData = last7DaysData,
                        title = "Last 7 Days Sales"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // All Time Sales Chart
                if (salesData.isNotEmpty()) {
                    SalesChart(
                        salesData = salesData,
                        title = "All Time Sales Trend"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Overall Statistics Card
            StatisticsCard(
                title = "Overall Performance",
                color = MaterialTheme.colorScheme.primary
            ) {
                StatisticRow("Total Orders", statistics.totalOrders.toString())
                StatisticRow("Total Revenue", "₱${"%.2f".format(statistics.totalRevenue)}")
                StatisticRow("Average Order Value", "₱${"%.2f".format(statistics.averageOrderValue)}")
                StatisticRow("Most Popular Item", "${statistics.mostPopularItem} (${statistics.mostPopularItemCount} sold)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Statistics
            StatisticsCard(
                title = "Today's Performance",
                color = MaterialTheme.colorScheme.secondary
            ) {
                StatisticRow("Orders Today", statistics.todayOrders.toString())
                StatisticRow("Revenue Today", "₱${"%.2f".format(statistics.todayRevenue)}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // This Week's Statistics
            StatisticsCard(
                title = "This Week's Performance",
                color = MaterialTheme.colorScheme.tertiary
            ) {
                StatisticRow("Orders This Week", statistics.thisWeekOrders.toString())
                StatisticRow("Revenue This Week", "₱${"%.2f".format(statistics.thisWeekRevenue)}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // This Month's Statistics
            StatisticsCard(
                title = "This Month's Performance",
                color = MaterialTheme.colorScheme.error
            ) {
                StatisticRow("Orders This Month", statistics.thisMonthOrders.toString())
                StatisticRow("Revenue This Month", "₱${"%.2f".format(statistics.thisMonthRevenue)}")
            }

            if (orderRecords.isEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No orders found. Start taking orders to see statistics!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun processOrdersForVisualization(orders: List<OrderRecord>): List<SalesDataPoint> {
    return orders
        .groupBy { order ->
            val calendar = Calendar.getInstance()
            calendar.time = order.timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }
        .map { (date, ordersForDate) ->
            SalesDataPoint(
                date = date,
                amount = ordersForDate.sumOf { it.totalAmount },
                itemCount = ordersForDate.sumOf { it.items.size }
            )
        }
        .sortedBy { it.date }
} 