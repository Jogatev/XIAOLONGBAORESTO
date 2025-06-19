package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderItem
import com.css152lgroup10.noodlemoneybuddy.data.models.OrderRecord
import com.css152lgroup10.noodlemoneybuddy.utils.AppDestinations

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentOrderItems: MutableState<List<OrderItem>>,
    orderRecords: MutableState<List<OrderRecord>>
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
                orderItems = currentOrderItems,
                onSaveOrder = { record ->
                    orderRecords.value = orderRecords.value + record
                    currentOrderItems.value = emptyList()
                }
            )
        }
        composable(AppDestinations.ORDER_RECORDS_SCREEN) {
            OrderRecordsScreen(
                navController = navController,
                orderRecords = orderRecords.value,
                onOrderClick = { orderId ->
                    navController.navigate("${AppDestinations.ORDER_DETAIL_SCREEN}/$orderId")
                },
                onDeleteOrder = { orderId ->
                    orderRecords.value = orderRecords.value.filterNot { it.id == orderId }
                }
            )
        }
        composable("${AppDestinations.ORDER_DETAIL_SCREEN}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            val record = orderRecords.value.find { it.id == orderId }
            if (record != null) {
                OrderDetailScreen(
                    navController = navController,
                    orderRecord = record,
                    onUpdateOrder = { updatedOrder ->
                        orderRecords.value = orderRecords.value.map {
                            if (it.id == updatedOrder.id) updatedOrder else it
                        }
                    },
                    onDeleteOrder = { deleteId ->
                        orderRecords.value = orderRecords.value.filterNot { it.id == deleteId }
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(AppDestinations.STATISTICS_SCREEN) {
            StatisticsScreen(
                navController = navController,
                orderRecords = orderRecords.value
            )
        }
    }
} 