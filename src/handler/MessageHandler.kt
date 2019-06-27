package com.example.handler

import com.example.Injector
import com.example.Injector.kodein
import com.example.MessageUsecase
import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Title
import com.example.usecase.MessageUsecaseImpl
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

data class MessageJson(val id: Int, val title: String)

data class MessageJsonList(val tests: List<MessageJson>)

data class InputMessageJson(val title: String)

data class JsonCountResponse(val message: String, val count: Int)

data class JsonErrorReponse(val reason: String)

fun Message.toJson(): MessageJson {
    return MessageJson(this.id.value, this.title.value)
}


fun Application.module() {
    val usecase: MessageUsecase by Injector.kodein.instance()

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/messages") {
            val messages = usecase.findAll()
            call.respond(MessageJsonList(messages.map { Message(it.id, it.title).toJson() }))
        }

        get("/messages/{id}") {
            call.parameters["id"]?.let { p ->
                runCatching {
                    p.toInt()
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorReponse("$p : validation error"))
                }.onSuccess { id ->
                    usecase.find(Id(id))?.let { m ->
                        call.respond(MessageJson(m.id.value, m.title.value))
                    } ?: run {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }

        get("/messages/search") {
            call.parameters["title"]?.let { t ->
                usecase.find(Title(t)).let { m ->
                    call.respond(MessageJsonList(m.map { Message(it.id, it.title).toJson() }))
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/messages/{id}/change") {
            call.parameters["id"]?.let { p ->
                runCatching {
                    p.toInt()
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorReponse("$p : validation error"))
                }.onSuccess { id ->
                    call.request.queryParameters["title"]?.let { title ->
                        val result = usecase.update(Message(Id(id), Title(title)))
                        call.respond(JsonCountResponse("update", result))
                    }
                }
            }
        }

        delete("messages/{id}") {
            call.parameters["id"]?.let { p ->
                runCatching {
                    p.toInt()
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorReponse("$p : validation error"))
                }.onSuccess { id ->
                    val result = usecase.delete(Id(id))
                    call.respond(JsonCountResponse("delete", result))
                }
            }
        }


        put("messages/{id}") {

            call.parameters["id"]?.let { p ->
                runCatching {
                    p.toInt()
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, JsonErrorReponse("$p : validation error"))
                }.onSuccess { id ->
                    runCatching {
                        val message = call.receive<InputMessageJson>()
                        usecase.upsert(Message(Id(id), Title(message.title)))
                    }.onFailure {
                        call.respond(HttpStatusCode.BadRequest, JsonErrorReponse("json : validation error"))
                    }.onSuccess { m ->
                        call.respond(emptyMap<String, String>())
                    }
                }
            }
        }
    }
}