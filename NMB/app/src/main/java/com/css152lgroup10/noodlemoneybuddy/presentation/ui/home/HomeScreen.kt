package com.css152lgroup10.noodlemoneybuddy.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val salesStats by viewModel.salesStatistics.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSalesStatistics("2025-06-01", "2025-06-12")
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text("📊 Sales Overview", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                if (salesStats != null) {
                    Text("Total Revenue: ${salesStats!!.getFormattedTotalRevenue()}")
                    Text("Total Orders: ${salesStats!!.totalOrders}")
                    Text("Total Items Sold: ${salesStats!!.totalItems}")
                    Text("Average Order Value: ${salesStats!!.getFormattedAverageOrderValue()}")
                    Text("Period: ${salesStats!!.period}")
                } else {
                    Text("No statistics available.")
                }
            }
        }
    }
}
