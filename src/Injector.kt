package com.example

import com.example.driver.DatasourceProvider
import com.example.driver.MysqlDatasourceProvider
import com.example.gateway.MessagePort
import com.example.gateway.MessageRepository
import com.example.usecase.MessageUsecaseImpl
import driver.MessageDao
import driver.MessageDriver
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton


object Injector {

    val usecaseModule = Kodein.Module(name = "Usecase") {
        bind<MessageUsecase>() with singleton { MessageUsecaseImpl(instance()) }
    }

    val portModule = Kodein.Module(name = "Port") {
        bind<MessagePort>() with singleton { MessageRepository(instance()) }
    }

    val driverModule = Kodein.Module(name = "Driver") {
        bind<MessageDriver>() with singleton { MessageDao(instance()) }
    }

    val datasourceModule = Kodein.Module(name = "Datasource") {
        bind<DatasourceProvider>() with singleton { MysqlDatasourceProvider() }
    }

    val kodein = Kodein {
        import(usecaseModule)
        import(portModule)
        import(driverModule)
        import(datasourceModule)
    }
}