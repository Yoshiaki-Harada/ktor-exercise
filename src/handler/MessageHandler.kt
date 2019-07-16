package com.example.handler

import com.example.Injector
import com.example.MessageUsecase
import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Title
import com.example.featureModule
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein


data class MessageJson(val id: Int, val title: String)

data class MessageJsonList(val tests: List<MessageJson>)

data class InputMessageJson(val title: String)

data class JsonCountResponse(val message: String, val count: Int)

data class JsonErrorsResponse(val reason: List<String>)

val emptyJson = emptyMap<String, String>()

fun Message.toJson(): MessageJson {
    return MessageJson(this.id.value, this.title.value)
}

@KtorExperimentalLocationsAPI
fun Application.messageModule() {
    featureModule()
    messageModuleWithDeps(Injector.kodein)
}

@KtorExperimentalLocationsAPI
fun Application.messageModuleWithDeps(kodein: Kodein) {


    val usecase: MessageUsecase by kodein.instance()


    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/messages") {
            val messages = usecase.findAll()
            call.respond(MessageJsonList(messages.map { Message(it.id, it.title).toJson() }))
        }


        @Location("/messages/{id}")
        data class IdLocation(val id: Int)

        val validateId = Validation<IdLocation> {
            IdLocation::id {
                minimum(0)
            }
        }
        get<IdLocation> { params ->
            if (validateId(params) is Valid) {
                val id = params.id
                usecase.find(Id(id))?.let { m ->
                    call.respond(MessageJson(m.id.value, m.title.value))
                } ?: run {
                    call.respond(HttpStatusCode.NotFound, emptyJson)
                }
            } else {
                val reason = validateId(params)[IdLocation::id] ?: listOf("Unknown Error")
                call.respond(HttpStatusCode.BadRequest, JsonErrorsResponse(reason))
            }
        }

        @Location("/messages/search")
        data class TitleLocation(val title: String)
        get<TitleLocation> { patams ->
            val title = patams.title
            usecase.find(Title(title)).let { m ->
                call.respond(MessageJsonList(m.map { Message(it.id, it.title).toJson() }))
            }
        }

        @Location("messages/{id}/change")
        data class ChangeTitleLocation(val id: Int, val title: String)

        val validateChangeTitle = Validation<ChangeTitleLocation> {
            ChangeTitleLocation::id {
                minimum(0)
            }
        }
        post<ChangeTitleLocation> { params ->
            if (validateChangeTitle(params) is Valid) {
                val id = params.id
                val title = params.title
                val result = usecase.update(Message(Id(id), Title(title)))
                call.respond(JsonCountResponse("update", result))
            } else {
                val reason = validateChangeTitle(params)[ChangeTitleLocation::id] ?: listOf("Unknown Error")
                call.respond(HttpStatusCode.BadRequest, JsonErrorsResponse(reason))
            }
        }

        @Location("message/{id}")
        data class DeleteIdLocation(val id: Int)

        val validateDeleteId = Validation<DeleteIdLocation> {
            DeleteIdLocation::id  {
                minimum(0)
            }
        }
        delete<DeleteIdLocation> { params ->
            if (validateDeleteId(params) is Valid) {
                val id = params.id
                val result = usecase.delete(Id(id))
                call.respond(JsonCountResponse("delete", result))
            } else {
                val reason = validateDeleteId(params)[DeleteIdLocation::id] ?: listOf("Unknown Error")
                call.respond(HttpStatusCode.BadRequest, JsonErrorsResponse(reason))
            }
        }

        @Location("messages/{id}")
        data class PutMessageLocation(val id: Int)

        val validatePutMessageLocation = Validation<PutMessageLocation> {
            PutMessageLocation::id {
                minimum(0)
            }
        }

        val validateInputMessage = Validation<InputMessageJson> {
            InputMessageJson::title required {}
        }

        put<PutMessageLocation> { params ->
            if (validatePutMessageLocation(params) is Valid) {
                val id = params.id
                val message = call.receive<InputMessageJson>()
                if (validateInputMessage(message) is Valid) {
                    usecase.upsert(Message(Id(id), Title(message.title)))
                    call.respond(emptyMap<String, String>())
                } else {
                    val reason = validateInputMessage(message) ?: listOf("Unknown Error")
                    call.respond(HttpStatusCode.BadRequest, reason)
                }
            } else {
                val reason = validatePutMessageLocation(params)[PutMessageLocation::id] ?: listOf("Unknown Error")
                call.respond(HttpStatusCode.BadRequest, JsonErrorsResponse(reason))
            }
        }
    }
}