package com.example.gateway

import com.example.domain.Id
import com.example.domain.Message
import com.example.domain.Messages
import com.example.domain.Title

interface MessagePort {
    fun findAll(): Messages
    fun find(id: Id): Message?
    fun find(title: Title): Messages
    fun upsert(message: Message)
    fun update(message: Message):Int
    fun delete(id: Id): Int
}