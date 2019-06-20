package com.example

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.apache.ApacheEngineConfig
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class Letter(val name: String, val message: String)

object Tests : Table() {
    val id = integer("id").primaryKey()
    val title = varchar("title", 20)
}


data class Test(val id: Int, val title: String)

fun Test.toJson(): TestJson {
    return TestJson(this.id, this.title)
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

    val client = HttpClient(Apache) {
        engine {

        }
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
                Tests.selectAll().map {
                    Test(it[Tests.id], it[Tests.title])
                }
            }

            call.respond(TestJsonList(tests.map { it.toJson() }))
        }

        get("/tests/{id}") {
            val id = call.parameters["id"]
            val test = transaction {
                addLogger(StdOutSqlLogger)
                Tests.select {
                    Tests.id eq id!!.toInt()
                }.map {
                    Test(it[Tests.id], it[Tests.title])
                }.first()
            }
            call.respond(TestJson(test.id, test.title))
        }

        post("/tests/{id}/change") {
            val id = call.parameters["id"]
            val title: String? = call.request.queryParameters["title"]
            val test = transaction {
                addLogger(StdOutSqlLogger)
                Tests.update({ Tests.id eq id!!.toInt() }) {
                    it[Tests.title] = title!!
                }
            }
            call.respondText("OK!", contentType = ContentType.Text.Plain)
        }

        delete("tests/{id}") {
            val id = call.parameters["id"]
            transaction(db) {
                Tests.deleteWhere { Tests.id eq id!!.toInt() }
            }
            call.respondText("Delete!", contentType = ContentType.Text.Plain)
        }

        put("tests/{id}") {
            val id = call.parameters["id"]
            val inputTestJson = call.receive<InputTestJson>()
            transaction(db) {
                if (Tests.select { Tests.id eq id!!.toInt() }.count() > 0) {
                    addLogger(StdOutSqlLogger)
                    Tests.update({ Tests.id eq id!!.toInt() }) {
                        it[Tests.title] = inputTestJson.title
                    }
                } else {
                    Tests.insert {
                        it[Tests.id] = id!!.toInt()
                        it[Tests.title] = inputTestJson.title
                    }
                }
            }
            call.respondText("OK!", contentType = ContentType.Text.Plain)
        }
    }

}

//object StarWarsFilms : StringTable() {
//    val sequelId = integer("sequel_id").uniqueIndex()
//    val name = varchar("name", 50)
//    val director = varchar("director", 50)
//}
//
//class StarWarsFilm(id: EntityID<String>) : StringEntity(id) {
//    companion object : StringEntityClass<StarWarsFilm>(StarWarsFilms)
//    var sequelId by StarWarsFilms.sequelId
//    var name     by StarWarsFilms.name
//    var director by StarWarsFilms.director
//}
//
//val movie = StarWarsFilm.new {
//    name = "The Last Jedi"
//    sequelId = 8
//    director = "Rian Johnson"
//}