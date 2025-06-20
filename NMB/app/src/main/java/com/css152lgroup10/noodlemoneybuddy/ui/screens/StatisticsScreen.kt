package com.css152lgroup10.noodlemoneybuddy.ui.screens
import kotlinx.coroutines.delay
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
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderWithItems
import com.css152lgroup10.noodlemoneybuddy.data.models.SalesDataPoint
import com.css152lgroup10.noodlemoneybuddy.ui.components.*
import com.css152lgroup10.noodlemoneybuddy.utils.calculateStatistics
import com.css152lgroup10.noodlemoneybuddy.utils.exportToCSV
import com.css152lgroup10.noodlemoneybuddy.utils.exportToExcel
import com.css152lgroup10.noodlemoneybuddy.utils.processOrdersForVisualization
import java.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ordersWithItems by orderViewModel.orders.collectAsState()
    val statistics = remember(ordersWithItems) { calculateStatistics(ordersWithItems) }
    var showExportMenu by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    // Process sales data for visualization
    val salesData = remember(ordersWithItems) { processOrdersForVisualization(ordersWithItems) }
    
    // Calculate daily averages for the last 7 days
    val last7DaysData = remember(ordersWithItems) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calendar.time
        ordersWithItems
            .filter { it.order.timestamp >= startDate }
            .let { processOrdersForVisualization(it) }
    }

    // Error and success message handling
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            delay(3000)
            errorMessage = null
        }
    }
    
    LaunchedEffect(showSuccessMessage) {
        showSuccessMessage?.let {
            delay(2000)
            showSuccessMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Statistics",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) 
                },
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
                                isLoading = true
                                coroutineScope.launch {
                                    val success = withContext(Dispatchers.IO) { exportToCSV(context, ordersWithItems) }
                                    isLoading = false
                                    if (success) {
                                        showSuccessMessage = "CSV exported successfully!"
                                    } else {
                                        errorMessage = "Export failed"
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Share, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export to Excel") },
                            onClick = {
                                showExportMenu = false
                                isLoading = true
                                coroutineScope.launch {
                                    val success = withContext(Dispatchers.IO) { exportToExcel(context, ordersWithItems) }
                                    isLoading = false
                                    if (success) {
                                        showSuccessMessage = "Excel file exported successfully!"
                                    } else {
                                        errorMessage = "Export failed"
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Error message
            errorMessage?.let { message ->
                ErrorState(
                    message = message,
                    onRetry = { errorMessage = null }
                )
            }

            // Success message
            showSuccessMessage?.let { message ->
                SuccessMessage(
                    message = message,
                    onDismiss = { showSuccessMessage = null }
                )
            }

            // Loading overlay
            LoadingOverlay(isLoading = isLoading)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Sales Visualization Section
                if (ordersWithItems.isNotEmpty()) {
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

                if (ordersWithItems.isEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    EmptyState(
                        title = "No Data Available",
                        message = "Start taking orders to see statistics and analytics here!",
                        icon = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "No data",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    )
                }
            }
        }
    }
} 