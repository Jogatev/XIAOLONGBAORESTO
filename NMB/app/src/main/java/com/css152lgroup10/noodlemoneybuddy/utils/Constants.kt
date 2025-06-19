package com.css152lgroup10.noodlemoneybuddy.utils

import com.css152lgroup10.noodlemoneybuddy.data.models.MenuItem

object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ORDER_LIST_SCREEN = "order_list"
    const val ORDER_RECORDS_SCREEN = "order_records"
    const val ORDER_DETAIL_SCREEN = "order_detail"
    const val STATISTICS_SCREEN = "statistics"
}

object MenuItems {
    val availableMenuItems = listOf(
        MenuItem("noodle_a", "Spicy Ramen", 250.00),
        MenuItem("noodle_b", "Beef Mami", 180.00),
        MenuItem("drink_c", "Iced Tea", 60.00),
        MenuItem("side_d", "Gyoza (3pcs)", 80.00),
        MenuItem("noodle_e", "Chicken Noodle Soup", 170.00),
        MenuItem("drink_f", "Coke", 50.00),
        MenuItem("side_g", "California Maki (4pcs)", 120.00)
    )
} 