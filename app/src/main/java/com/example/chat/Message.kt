package com.example.chat

import java.net.URL
import java.util.*

class Message (
        val text: String,
        val author: String,
        val date: Date,
        val authorId: Long,
        val messageId: Long,
        val avatarUrl: String? = null,
        val reactions: List<Pair<Emoji, Int>> = emptyList()
        )