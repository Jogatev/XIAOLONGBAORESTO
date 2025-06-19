package com.css152lgroup10.noodlemoneybuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.css152lgroup10.noodlemoneybuddy.utils.AppDestinations
import com.css152lgroup10.noodlemoneybuddy.utils.ClickDebouncer

@Composable
fun MenuScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val lessRoundedButtonShape = RoundedCornerShape(8.dp)
    val debouncer = remember { ClickDebouncer() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                debouncer.processClick {
                    navController.navigate(AppDestinations.ORDER_LIST_SCREEN) {
                        launchSingleTop = true
                    }
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) { Text("Create Order") }

        Button(
            onClick = {
                debouncer.processClick {
                    navController.navigate(AppDestinations.ORDER_RECORDS_SCREEN)
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        ) { Text("Modify Order") }

        Button(
            onClick = {
                debouncer.processClick {
                    navController.navigate(AppDestinations.STATISTICS_SCREEN)
                }
            },
            shape = lessRoundedButtonShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(vertical = 8.dp)
        ) { Text("View Statistics") }
    }
} 