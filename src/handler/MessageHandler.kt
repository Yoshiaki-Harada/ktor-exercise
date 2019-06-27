package com.example.handler

import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Title
import com.example.usecase.MessageUsecase
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

data class MessageJson(val id: Int, val title: String)

data class MessageJsonList(val tests: List<MessageJson>)

data class InputMessageJson(val title: String)

fun Message.toJson(): MessageJson {
    return MessageJson(this.id.value, this.title.value)
}


fun Application.module() {
    val usecase = MessageUsecase

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/messages") {
            val messages = usecase.findAll()
            call.respond(MessageJsonList(messages.map { Message(it.id, it.title).toJson() }))
        }

        get("/messages/{id}") {
            call.parameters["id"]?.let {
                usecase.find(Id(it.toInt()))?.let { m ->
                    call.respond(MessageJson(m.id.value, m.title.value))
                }
            }
        }

        post("/messages/{id}/change") {
            call.parameters["id"]?.toInt()?.let { id ->
                call.request.queryParameters["title"]?.let { title ->
                    usecase.update(Message(Id(id), Title(title)))
                    call.respondText("update!", contentType = ContentType.Text.Plain)
                }
            }
        }

        delete("messages/{id}") {
            call.parameters["id"]?.let {
                it.toInt().let { id ->
                    val result = usecase.delete(Id(id))
                    call.respondText("Delete!$result", contentType = ContentType.Text.Plain)
                }
            }
        }

        put("messages/{id}") {
            val inputMessageJson = call.receive<InputMessageJson>()
            call.parameters["id"]?.let {
                usecase.upsert(Message(Id(it.toInt()), Title(inputMessageJson.title)))
                call.respondText("OK!", contentType = ContentType.Text.Plain)
            }
        }
    }
}