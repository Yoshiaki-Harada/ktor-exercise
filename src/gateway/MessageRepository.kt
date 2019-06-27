package com.example.gateway

import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title
import driver.MessageDao
import driver.MessageDriver
import driver.MessageEntityEntity

object MessageRepository : MessagePort {
    val messageDriver: MessageDriver = MessageDao

    override fun findAll(): Messages {
        return Messages(messageDriver.findAll().map { Message(Id(it.id), Title(it.title)) })
    }

    override fun find(id: Id): Message? {
        return messageDriver.find(id.value)?.let { Message(Id(it.id), Title(it.title)) }
    }

    override fun find(title: Title): Messages {
        return Messages(messageDriver.find(title.value).map { Message(Id(it.id), Title(it.title)) })
    }

    override fun upsert(message: Message) {
        var messageEntity = MessageEntityEntity()
        messageEntity.id = message.id.value
        messageEntity.title = message.title.value
        return messageDriver.upsert(messageEntity)
    }

    override fun update(message: Message) {
        var messageEntity = MessageEntityEntity()
        messageEntity.id = message.id.value
        messageEntity.title = message.title.value
        return messageDriver.update(messageEntity)
    }

    override fun delete(id: Id): Int {
        return messageDriver.delete(id.value)
    }
}