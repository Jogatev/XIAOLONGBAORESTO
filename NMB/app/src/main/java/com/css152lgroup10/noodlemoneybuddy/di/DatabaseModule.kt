package com.css152lgroup10.noodlemoneybuddy.di

import android.app.Application
import androidx.room.Room
import com.css152lgroup10.noodlemoneybuddy.data.local.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "noodle_money_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().orderDao() }
}
