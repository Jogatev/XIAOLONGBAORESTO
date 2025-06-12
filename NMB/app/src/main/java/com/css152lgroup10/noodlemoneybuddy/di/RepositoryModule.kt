package com.css152lgroup10.noodlemoneybuddy.di

import com.css152lgroup10.noodlemoneybuddy.data.repository.OrderRepository
import com.css152lgroup10.noodlemoneybuddy.domain.repository.IOrderRepository
import com.css152lgroup10.noodlemoneybuddy.data.local.OrderDao
import org.koin.core.scope.get
import org.koin.dsl.module
import com.google.gson.Gson

val repositoryModule = module {
    single<IOrderRepository> { OrderRepository() }
}