package com.css152lgroup10.noodlemoneybuddy.presentation.ui.order.create.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.css152lgroup10.noodlemoneybuddy.data.model.MenuItem

@Composable
fun MenuItemCard(menuItem: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(menuItem.name, style = MaterialTheme.typography.titleMedium)
            Text("₱%.2f".format(menuItem.price), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
