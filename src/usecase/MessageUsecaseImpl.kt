package com.example.usecase

import com.example.Injector
import com.example.MessageUsecase
import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title
import com.example.gateway.MessagePort
import com.example.gateway.MessageRepository
import driver.MessageDriver
import org.kodein.di.generic.instance

class MessageUsecaseImpl(private val port: MessagePort) : MessageUsecase {

    override fun findAll(): Messages {
        return port.findAll()
    }

    override fun find(id: Id): Message? {
        return port.find(id)
    }

    override fun find(title: Title): Messages {
        return port.find(title)
    }

    override fun upsert(message: Message) {
        port.upsert(message)
    }

    override fun update(message: Message):Int {
        return port.update(message)
    }

    override fun delete(id: Id): Int {
        return port.delete(id)
    }
}