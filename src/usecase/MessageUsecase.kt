package com.example.usecase

import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title
import com.example.gateway.MessagePort
import com.example.gateway.MessageRepository

object MessageUsecase {
    val port: MessagePort = MessageRepository
    fun findAll(): Messages {
        return port.findAll()
    }

    fun find(id: Id): Message? {
        return port.find(id)
    }

    fun find(title: Title): Messages {
        return port.find(title)
    }

    fun upsert(message: Message) {
        port.upsert(message)
    }

    fun update(message: Message) {
        port.update(message)
    }

    fun delete(id: Id): Int {
        return port.delete(id)
    }
}