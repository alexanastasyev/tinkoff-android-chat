package com.example.chat

import java.io.Serializable
import java.net.URL
import java.util.*

class Message (
        val text: String,
        val author: String,
        val date: Date,
        val authorId: Long,
        val messageId: Long,
        val avatarUrl: String? = null,
        var reactions: List<Reaction> = emptyList()
        ) : Serializable