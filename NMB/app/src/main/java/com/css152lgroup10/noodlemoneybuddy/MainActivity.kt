package com.css152lgroup10.noodlemoneybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.css152lgroup10.noodlemoneybuddy.ui.screens.AppNavigation
import com.css152lgroup10.noodlemoneybuddy.ui.screens.OrderViewModel
import com.css152lgroup10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoodleMoneyBuddyTheme {
                val navController = rememberNavController()
                val orderViewModel: OrderViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        orderViewModel = orderViewModel
                    )
                }
            }
        }
    }
}