package com.example.handler

import com.example.Injector
import com.example.MessageUsecase
import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.ktor.server.testing.withTestApplication
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import io.ktor.client.tests.utils.main
import io.ktor.server.testing.TestApplicationEngine
import com.example.featureModule
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.handleRequest


class MessageHandlerKtTest : StringSpec() {

    init {
        "Find All" {
            withTestApplication {
                var child = Kodein {
                    extend(Injector.kodein)
                    bind<MessageUsecase>(overrides = true) with singleton { TestMessageUsecase() }
                }
                this.application.featureModule()
                this.application.messageModuleWithDeps(child)
                with(handleRequest(HttpMethod.Get, "/messages")) {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe "{}"
                }
            }
        }
    }

}


class TestMessageUsecase : MessageUsecase {
    override fun find(id: Id): Message? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun find(title: Title): Messages {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun upsert(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(message: Message): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: Id): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): Messages {
        return Messages(listOf(Message(id = Id(0), title = Title("test"))))
    }
}