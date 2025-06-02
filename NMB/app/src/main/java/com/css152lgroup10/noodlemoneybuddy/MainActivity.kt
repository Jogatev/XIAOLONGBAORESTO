package com.css152lgroup10.noodlemoneybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import com.css152lgroup10.noodlemoneybuddy.ui.screens.OrderListScreen
import com.css152lgroup10.noodlemoneybuddy.ui.theme.POSSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            POSSystemTheme {
                Surface {
                    OrderListScreen()
                }
            }
        }
    }
}
