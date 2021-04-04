package com.example.chat.entities

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class Message(
    val text: String,
    val author: String,
    val date: Date,
    val authorId: Long,
    var messageId: Long,
    val avatarUrl: String? = null,
    var reactions: ArrayList<Reaction> = arrayListOf()
) : Serializable