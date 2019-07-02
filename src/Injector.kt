package com.example

import com.example.gateway.MessagePort
import com.example.gateway.MessageRepository
import com.example.usecase.MessageUsecaseImpl
import com.google.gson.Gson
import com.mysql.cj.jdbc.MysqlDataSource
import com.typesafe.config.ConfigFactory
import driver.MessageDao
import driver.MessageDriver
import driver.Models
import io.github.config4k.extract
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.io.File
import java.net.URL


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
        bind<KotlinEntityDataStore<Persistable>>() with singleton {
            val dataSource = MysqlDataSource()
            val user by kodein.instance<String>(tag = "user")
            val password by kodein.instance<String>(tag = "password")
            val url by kodein.instance<String>(tag = "url")

            dataSource.setURL(url)
            dataSource.user = user
            dataSource.password = password
            val configuration = KotlinConfiguration(
                model = Models.DEFAULT,
                dataSource = dataSource,
                statementCacheSize = 0,
                useDefaultLogging = true
            )
            SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)
            KotlinEntityDataStore<Persistable>(configuration)
        }
    }

    data class User(val name: String, val pass: String, val url: String)

    val userModule = Kodein.Module(name = "User") {
        javaClass.getResource("/user.conf")?.let {
            val user = ConfigFactory.parseURL(it).extract<User>("user")
            println("user $user")
            bind<String>(tag = "user") with singleton { user.name }
            bind<String>(tag = "password") with singleton { user.pass }
            bind<String>(tag = "url") with singleton { user.url }
        } ?: run {
            throw Throwable("/user.conf not found")
        }
    }

    val kodein = Kodein {
        importAll(listOf(usecaseModule, portModule, driverModule, datasourceModule, userModule), true)
    }
}