package com.css152lgroup10.noodlemoneybuddy.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.home.HomeScreen
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.CreateOrderScreen
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.modify.OrderListScreen
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.statistics.StatisticsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.CreateOrder.route) { CreateOrderScreen() }
        composable(Screen.ModifyOrder.route) {
            OrderListScreen(onOrderSelected = { /* handle nav if needed */ })
        }
        composable(Screen.Statistics.route) { StatisticsScreen() }
    }
}