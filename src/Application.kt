package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.request.receive
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class Letter(val name: String, val message: String)

object Tests : IntIdTable() {
    val title = varchar("title", 20)
}

class Test(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Test>(Tests)
    
    var title by Tests.title
}

fun Test.toJson(): TestJson {
    return TestJson(this.id.value, this.title)
}

data class TestJson(val id: Int, val title: String)

data class TestJsonList(val tests: List<TestJson>)

data class InputTestJson(val title: String)

val db = connect("jdbc:mysql://localhost:3306/test", "com.mysql.jdbc.Driver", "test", "test")

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    val client = HttpClient() {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(Letter("Taro", "hello"))
        }

        get("/tests") {
            val tests = transaction(db) {
                addLogger(StdOutSqlLogger)
                Test.all().toList()
            }
            call.respond(TestJsonList(tests.map { it.toJson() }))
        }

        get("/tests/{id}") {
            val id = call.parameters["id"]
            val test = transaction {
                addLogger(StdOutSqlLogger)
                Test.findById(id!!.toInt())
            }
            call.respond(TestJson(test!!.id.value, test.title))
        }

        post("/tests/{id}/change") {
            val id = call.parameters["id"]
            val title: String? = call.request.queryParameters["title"]
            val test = transaction {
                addLogger(StdOutSqlLogger)
                Test.findById(id!!.toInt())?.title = title!!
            }
            call.respondText("OK!", contentType = ContentType.Text.Plain)
        }

        delete("tests/{id}") {
            val id = call.parameters["id"]
            transaction(db) {
                Test.findById(id!!.toInt())?.delete()
            }
            call.respondText("Delete!", contentType = ContentType.Text.Plain)
        }

        put("tests/{id}") {
            val id = call.parameters["id"]
            val inputTestJson = call.receive<InputTestJson>()
            transaction(db) {
                Test.findById(id!!.toInt())?.let {
                    it.title == inputTestJson.title
                } ?: run {
                    Test.new {
                        title = inputTestJson.title
                    }
                }
            }
            call.respondText("OK!", contentType = ContentType.Text.Plain)
        }
    }
}

