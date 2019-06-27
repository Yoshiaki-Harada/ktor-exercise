package com.example.gateway

import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title
import driver.MessageDriver
import driver.MessageEntityEntity

class MessageRepository(private val driver: MessageDriver) : MessagePort {

    override fun findAll(): Messages {
        return Messages(driver.findAll().map { Message(Id(it.id), Title(it.title)) })
    }

    override fun find(id: Id): Message? {
        return driver.find(id.value)?.let { Message(Id(it.id), Title(it.title)) }
    }

    override fun find(title: Title): Messages {
        return Messages(driver.find(title.value).map { Message(Id(it.id), Title(it.title)) })
    }

    override fun upsert(message: Message) {
        var messageEntity = MessageEntityEntity()
        messageEntity.id = message.id.value
        messageEntity.title = message.title.value
        return driver.upsert(messageEntity)
    }

    override fun update(message: Message): Int {
        var messageEntity = MessageEntityEntity()
        messageEntity.id = message.id.value
        messageEntity.title = message.title.value
        return driver.update(messageEntity)
    }

    override fun delete(id: Id): Int {
        return driver.delete(id.value)
    }
}