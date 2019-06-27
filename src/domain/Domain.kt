package com.example.domain

import com.example.FCC

data class Id(val value: Int)
data class Title(val value: String)
data class Message(val id: Id, val title: Title)
data class Messages(override val list: List<Message>) : FCC<Message>