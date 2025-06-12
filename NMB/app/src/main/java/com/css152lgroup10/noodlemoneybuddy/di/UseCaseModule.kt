package com.css152lgroup10.noodlemoneybuddy.di

import com.css152lgroup10.noodlemoneybuddy.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    factory { CreateOrderUseCase(get()) }
    factory { ModifyOrderUseCase(get()) }
    factory { GenerateStatisticsUseCase(get()) }
    factory { ProcessPaymentUseCase(get()) }

}
