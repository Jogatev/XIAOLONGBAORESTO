package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.css152lgroup10.noodlemoneybuddy.utils.AppDestinations

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.MENU_SCREEN,
        modifier = modifier
    ) {
        composable(AppDestinations.MENU_SCREEN) {
            MenuScreen(navController = navController)
        }
        composable(AppDestinations.ORDER_LIST_SCREEN) {
            OrderListScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
        composable(AppDestinations.ORDER_RECORDS_SCREEN) {
            OrderRecordsScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
        composable("${AppDestinations.ORDER_DETAIL_SCREEN}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            if (orderId != null) {
                OrderDetailScreen(
                    navController = navController,
                    orderId = orderId,
                    orderViewModel = orderViewModel
                )
            } else {
                // Fallback UI for missing orderId
                androidx.compose.material3.Text("Order not found.")
            }
        }
        composable(AppDestinations.STATISTICS_SCREEN) {
            StatisticsScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
    }
} 