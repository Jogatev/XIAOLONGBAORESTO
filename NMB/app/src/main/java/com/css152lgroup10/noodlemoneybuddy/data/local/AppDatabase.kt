package com.css152lgroup10.noodlemoneybuddy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.css152lgroup10.noodlemoneybuddy.data.local.OrderDao
import com.css152lgroup10.noodlemoneybuddy.data.local.OrderEntity

@Database(
    entities = [OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
