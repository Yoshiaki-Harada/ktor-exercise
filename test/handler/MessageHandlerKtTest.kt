package handler

import com.example.Injector
import com.example.MessageUsecase
import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title
import com.example.featureModule
import com.example.handler.messageModule
import com.example.handler.messageModuleWithDeps
import io.ktor.application.Application
import io.ktor.client.tests.utils.main
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.apache.http.client.methods.HttpGet

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import kotlin.test.*

internal class MessageHandlerKtTest {
    @Test
    fun testRequest() = withTestApplication({
        var child = Kodein {
            extend(Injector.kodein)
            bind<MessageUsecase>(overrides = true) with singleton { TestMessageUsecase() }
        }
        featureModule()
        messageModuleWithDeps(child)
    }) {
        with(handleRequest(HttpMethod.Get, "/messages")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(
                "{}", response.content
            )
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