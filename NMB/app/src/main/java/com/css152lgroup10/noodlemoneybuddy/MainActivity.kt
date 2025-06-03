package com.css1521group10.noodlemoneybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.css1521group10.noodlemoneybuddy.ui.screens.POSSystemScreen
import com.css1521group10.noodlemoneybuddy.ui.theme.NoodleMoneyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoodleMoneyBuddyTheme {
                POSSystemScreen()
            }
        }
    }
}