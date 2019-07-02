package com.example

import com.example.handler.JsonErrorRespons
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Locations
import io.ktor.response.respond

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.featureModule() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Locations) {

    }
//    install(StatusPages) {
//        exception<Throwable> { cause ->
//            val errorMessage: String = cause.message ?: "Unknown Error"
//            call.respond(HttpStatusCode.InternalServerError, JsonErrorRespons(errorMessage))
//        }
//    }
}

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
        engine {

        }
    }
}

