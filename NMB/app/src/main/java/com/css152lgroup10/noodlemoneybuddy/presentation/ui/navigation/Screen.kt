package com.css152lgroup10.noodlemoneybuddy.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateOrder : Screen("create_order")
    object ModifyOrder : Screen("modify_order")
    object Statistics : Screen("statistics")
}
