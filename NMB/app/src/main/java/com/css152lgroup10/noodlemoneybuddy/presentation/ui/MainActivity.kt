package com.css152lgroup10.noodlemoneybuddy.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.navigation.NavigationGraph
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.navigation.Screen
import com.css152lgroup10.noodlemoneybuddy.presentation.ui.theme.NoodleMoneyBuddyTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.BarChart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoodleMoneyBuddyTheme {
                val navController = rememberNavController()
                val screens = listOf(
                    Screen.Home,
                    Screen.CreateOrder,
                    Screen.Statistics
                )

                // Get current destination to sync with bottom bar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    selected = currentDestination == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = when (screen) {
                                                Screen.Home -> Icons.Default.Home
                                                Screen.CreateOrder -> Icons.Default.ShoppingCart
                                                Screen.Statistics -> Icons.Default.BarChart
                                                else -> Icons.Default.Home
                                            },
                                            contentDescription = when (screen) {
                                                Screen.Home -> "Home"
                                                Screen.CreateOrder -> "Create Order"
                                                Screen.Statistics -> "Statistics"
                                                else -> "Navigation"
                                            }
                                        )
                                    },
                                    label = {
                                        Text(when (screen) {
                                            Screen.Home -> "Home"
                                            Screen.CreateOrder -> "Create Order"
                                            Screen.Statistics -> "Statistics"
                                            else -> "Tab"
                                        })
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavigationGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}